import java.io.File;
import java.util.Scanner;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;

public class Main {

    public static void main(String[] args) throws Exception {
		// Разбор CLI
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		options.addOption( OptionBuilder.withLongOpt("input")
										.hasArg()
										.withDescription("входной файл")
										.create("i") );
		options.addOption( OptionBuilder.withLongOpt("output")
										.hasArg()
										.withDescription("выходной файл")
										.create("o") );
        options.addOption( OptionBuilder.withLongOpt("verbose")
                                        .withDescription("вербальный режим")
                                        .create("v") );

		CommandLine line = parser.parse(options, args);

		String input, output;
        boolean verbose;

		// Имя входного файла
		if (line.hasOption("input"))
            input = line.getOptionValue("input");
		else
			input = null;

        verbose = line.hasOption("verbose");

        output = perform(input, verbose);

	    // Вывод
		if (line.hasOption("output")) {
			try( PrintWriter out = new PrintWriter(line.getOptionValue("output")) ) {
				out.println(output);
			}
		} else {
			System.out.println(output);
		}
    }

	public static String perform(String inputFileName, boolean verbose) {
		try {
            SimplexTable simplexTable = new SimplexTable(createSimplexTable(inputFileName));

            HashMap solution = solve(simplexTable);
            String answer = (String) solution.get("answer");
            String steps = "";

            if (verbose) {
                ArrayList<String> stepList = (ArrayList<String>) solution.get("step_list");

                int stepNumber = 1;
                for (String step : stepList) {
                    steps += highlight("Шаг " + stepNumber) + "\n" + step + "\n";
                    stepNumber++;
                }
            }

	       	return highlight("Задача") + "\n" +
                    simplexTable.problemString() + "\n" +
                    steps +
                    highlight("Решение") + "\n" +
                    answer;
		} catch (IOException e) {
			return "Файл не найден: " + inputFileName;
		}
	}

    public static String perform(String inputFileName) {
        return perform(inputFileName, false);
    }

    public static HashMap solve(SimplexTable simplexTable) {
        int cols = simplexTable.cols();
        int rows = simplexTable.rows();
        int resCol, resRow;
        int [] rowNames = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;
        HashMap result = new HashMap();
		String answer = "";
        ArrayList<String> stepList = new ArrayList(1);

        for (int j = 0; j < rows; j++)
            rowNames[j] = cols + j + 1;

        stepList.add(simplexTable.toString());

        while (!solved) {
            resCol = simplexTable.findResCol();
            if (resCol == -1) {
                result.put("answer", "Функция не ограничена\n");
                return result;
            } else if (resCol == 0) {
                solved = true;
            } else {
                resRow = simplexTable.findResRow(resCol);
                rowNames[resRow] = resCol;
                simplexTable.step(resRow, resCol);
                stepList.add(simplexTable.toString());
            }
        }

        for (int i = 1; i <= cols; i++) {
            int j = 0;
            for (; j < rows && rowNames[j] != i; j++);
			answer += "x" + i + " = ";
            if (j == rows) {
				answer += "0\n";
            }
            else {
				answer += simplexTable.getElement(j, 0) + "\n";
            }
        }

		answer += "max{ F(x) } = " + simplexTable.getElement(rows, 0);
        result.put("answer", answer);
        result.put("step_list", stepList);

		return result;
    }

    public static double[][] createSimplexTable(String fileName) throws IOException {

        Scanner scanner;

        if (fileName == null) {
            scanner = new Scanner(System.in).useLocale(new Locale("US"));
        } else {
            scanner = new Scanner(new File(fileName)).useLocale(new Locale("US"));
        }

        if (fileName == null) System.out.print("Количество переменных: ");
        int cols = scanner.nextInt();

        if (fileName == null) System.out.print("Количество ограничений: ");
        int rows = scanner.nextInt();

        double [][] simplexTable = new double[rows + 1][cols + 1];

        simplexTable[rows][0] = 0;
        if (fileName == null) System.out.println(highlight("\nЦелевая функция"));
        for (int i = 1; i <= cols; ++i) {
            if (fileName == null) System.out.print("  Коэффициент #" + i + ": ");
            simplexTable[rows][i] = -scanner.nextDouble();
        }
        if (fileName == null) System.out.println(highlight("\nКоэффициенты ограничений"));
        for (int i = 0; i < rows; ++i) {
            if (fileName == null) System.out.println("  Ограничение #" + (i + 1) + ":");
            for (int j = 1; j <= cols; ++j) {
                if (fileName == null) System.out.print("    Коэффициент при x" + j + ": ");
                simplexTable[i][j] = scanner.nextDouble();
            }
        }
        if (fileName == null) System.out.println(highlight("\nСвободные члены ограничений"));
        for (int i = 0; i < rows; ++i) {
            if (fileName == null) System.out.print("  Ограничение #" + (i+1) + ": ");
            simplexTable[i][0] = scanner.nextDouble();
        }

        if (fileName == null) System.out.println("");
        return simplexTable;

    }

    private static String highlight(String line) {
        return "\033[7m" + line + "\033[0m";
    }

}
