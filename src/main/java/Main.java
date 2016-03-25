import java.io.File;
import java.util.Scanner;
import java.util.Locale;
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
										.withDescription("input file")
										.create("i") );
		options.addOption( OptionBuilder.withLongOpt("output")
										.hasArg()
										.withDescription("output file")
										.create("o") );

		CommandLine line = parser.parse(options, args);

		String input, output;

		// Имя входного файла
		if (line.hasOption("input"))
            input = line.getOptionValue("input");
		else
			input = null;

        output = perform(input);

	  	// Вывод
		if (line.hasOption("output")) {
			try( PrintWriter out = new PrintWriter(line.getOptionValue("output")) ) {
				out.println(output);
			}
		} else {
			System.out.println(output);
		}
    }

	public static String perform(String inputFileName) {
		try {
            SimplexTable simplexTable = new SimplexTable(createSimplexTable(inputFileName));
	       	return simplexTable.problemString() + "\nSolution:\n\n" + solve(simplexTable);
		} catch (IOException e) {
			return "File not found: " + inputFileName;
		}
	}

    private static String solve(SimplexTable simplexTable) {
        int cols = simplexTable.cols();
        int rows = simplexTable.rows();
        int resCol, resRow;
        int [] rowNames = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;
		String answer = "";

        for (int j = 0; j < rows; j++)
            rowNames[j] = cols + j + 1;

        while (!solved) {
            resCol = simplexTable.findResCol();
            if (resCol == -1) {
				return "Objective function is unlimited\n";
            } else if (resCol == 0) {
                solved = true;
            } else {
                resRow = simplexTable.findResRow(resCol);
                rowNames[resRow] = resCol;
                simplexTable.step(resRow, resCol);
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
		answer += "max{ F(x) } = " + simplexTable.getElement(rows, 0) + '\n';
		return answer;
    }

    private static double[][] createSimplexTable(String fileName) throws IOException {

        Scanner scanner;

        if (fileName == null) {
            scanner = new Scanner(System.in).useLocale(new Locale("US"));
        } else {
            scanner = new Scanner(new File(fileName)).useLocale(new Locale("US"));
        }

        if (fileName == null) System.out.print("Number of variables: ");
        int cols = scanner.nextInt();

        if (fileName == null) System.out.print("Number of limitations: ");
        int rows = scanner.nextInt();

        double [][] simplexTable = new double[rows + 1][cols + 1];

        simplexTable[rows][0] = 0;
        if (fileName == null) System.out.println("\nCOST FUNCTION");
        for (int i = 1; i <= cols; ++i) {
            if (fileName == null) System.out.print("  Multiplier #" + i + ": ");
            simplexTable[rows][i] = -scanner.nextDouble();
        }
        if (fileName == null) System.out.println("\nLIMITATIONS: MULTIPLIERS");
        for (int i = 0; i < rows; ++i) {
            if (fileName == null) System.out.println("  Limitation #" + (i + 1) + ":");
            for (int j = 1; j <= cols; ++j) {
                if (fileName == null) System.out.print("    Multiplier #" + j + ": ");
                simplexTable[i][j] = scanner.nextDouble();
            }
        }
        if (fileName == null) System.out.println("\nLIMITATIONS: FREE TERMS");
        for (int i = 0; i < rows; ++i) {
            if (fileName == null) System.out.print(" Limitation #" + (i+1) + ": ");
            simplexTable[i][0] = scanner.nextDouble();
        }

        if (fileName == null) System.out.println("");
        return simplexTable;

    }

}
