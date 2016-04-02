package com.akxcv.jsimplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.akxcv.jsimplex.exception.InputException;
import com.akxcv.jsimplex.exception.LimitationException;
import com.akxcv.jsimplex.problem.Answer;
import com.akxcv.jsimplex.problem.Problem;
import com.akxcv.jsimplex.problem.SimplexTable;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) {
        HashMap options = null;

        try {
            options = parseCommandLine(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        Input input = null;
        try {
            input = getUserInput(options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (options.containsKey("debug") && options.get("debug").equals(true))
                e.printStackTrace();
            return;
        }

        Problem problem = createProblem(input);

        Answer answer = problem.solve();

        /*

		String input, output;

		// Имя входного файла
		if (line.hasOption("input"))
            input = line.getOptionValue("input");
		else
			input = null;


        output = perform(input, line.hasOption("verbose"), line.hasOption("integer"), line.hasOption("csv"), line.hasOption("min"));

	    // Вывод
		if (line.hasOption("output")) {
			try( PrintWriter out = new PrintWriter(line.getOptionValue("output")) ) {
				out.println(output);
			}
		} else {
			System.out.println(output);
		}*/
    }

    private static Problem createProblem(Input input) {
        int rows = input.getLimitations().length + 1;
        int cols = input.getCostFunction().getCoefCount() + 1;
        double [][] table = new double[rows][cols];

        table[rows - 1][0] = 0;
        for (int i = 1; i < cols; ++i) {
            table[rows - 1][i] = -input.getCostFunction().getCoef(i - 1);
        }

        for (int i = 0; i < rows - 1; ++i) {
            for (int j = 1; j < cols; ++j)
                table[i][j] = input.getLimitations()[i].getCoef(j - 1);
        }

        for (int i = 0; i < rows; ++i) {
            table[i][0] = input.getLimitations()[i].getFreeTerm();
        }

        return new Problem(new SimplexTable(table));
    }
	
	private static HashMap<String, Object> parseCommandLine(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
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
        options.addOption( OptionBuilder.withLongOpt("integer")
                                        .withDescription("округление ответа до целых чисел")
                                        .create() );
        options.addOption( OptionBuilder.withLongOpt("csv")
                                        .withDescription("запись в csv-файл")
                                        .create("c") );
        options.addOption( OptionBuilder.withLongOpt("debug")
                                        .withDescription("режим отладки")
                                        .create("d") );

		CommandLine line = parser.parse(options, args);
		HashMap<String, Object>  optionHash = new HashMap<>();

        if (line.hasOption("csv") && !line.hasOption("output"))
            throw new ParseException("Опция -o (--output) обязательна при использовании опции -c (--csv)!");

        if (line.hasOption("min") && line.hasOption("max"))
            throw new ParseException("Опции --max и --min несовместимы!");
		
		if (line.hasOption("input"))
			optionHash.put("input", line.getOptionValue("input"));
		if (line.hasOption("output"))
			optionHash.put("output", line.getOptionValue("output"));
		optionHash.put("verbose", line.hasOption("verbose"));
		optionHash.put("integer", line.hasOption("integer"));
		optionHash.put("csv", line.hasOption("csv"));
		optionHash.put("min", line.hasOption("min"));
        optionHash.put("debug", line.hasOption("debug"));
		
		return optionHash;
	}

    private static Input getUserInput(HashMap options) throws FileNotFoundException, LimitationException, InputException {
        if (options.containsKey("input")) {
            return getUserFileInput(options.get("input").toString());
        } else {
            return getUserKeyboardInput();
        }
    }

    private static Input getUserFileInput(String fileName) throws LimitationException, FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName)).useLocale(new Locale("US"));

        String line = scanner.nextLine();
        ArrayList<Limitation> limitations = new ArrayList<>();
        CostFunction costFunction = stringToCostFunction(line);

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!line.isEmpty())
                limitations.add(stringToLimitation(line));
        }

        return new Input(costFunction, limitations.toArray(new Limitation[0]));
    }

    private static Input getUserKeyboardInput() throws InputException, LimitationException {
        Scanner scanner = new Scanner(System.in).useLocale(new Locale("US"));

        String line;
        CostFunction costFunction;
        ArrayList<Limitation> limitations = new ArrayList<>();

        System.out.println("Введите условие задачи. Для окончания ввода нажмите Enter в пустой строке.");
        System.out.print("Целевая функция: ");
        line = scanner.nextLine();

        if (line.isEmpty()) {
            throw new InputException("Пустой ввод.");
        }

        costFunction = stringToCostFunction(line);

        while(true) {
            System.out.print("Ограничение: ");
            line = scanner.nextLine();
            if (line.isEmpty())
                break;
            limitations.add(stringToLimitation(line));
        }

        return new Input(costFunction, limitations.toArray(new Limitation[0]));
    }

    private static CostFunction stringToCostFunction(String string) {
        string = string.replaceAll("\\s", "");
        String[] atoms = string.split("\\-\\->|\\->|(?=\\+)|(?=\\-)");
        Pattern p = Pattern.compile("((?:\\-)?\\d+(?:\\.\\d+)?)");

        double[] coefs = new double[atoms.length - 1];
        int coefsCount = 0;

        for (String atom : atoms) {
            Matcher m = p.matcher(atom);
            if (m.find())
                coefs[coefsCount] = Double.parseDouble(m.group(0));

            coefsCount++;
        }

        return new CostFunction(coefs, atoms[coefsCount - 1].equals("min"));
    }

    private static Limitation stringToLimitation(String string) throws LimitationException {
        Limitation.LimitationSign sign;

        if (string.contains("<=")) {
            if (string.contains(">="))
                throw new LimitationException("Неверный знак ограничения");
            sign = Limitation.LimitationSign.LE;
        } else if (string.contains(">=")) {
            sign = Limitation.LimitationSign.ME;
        } else
            sign = Limitation.LimitationSign.EQ;

        string = string.replaceAll("\\s|<|>", "");
        String[] atoms = string.split("=|(?=\\+)|(?=\\-)");
        Pattern p = Pattern.compile("((?:\\-)?\\d+(?:\\.\\d+)?)");

        double[] coefs = new double[atoms.length - 1];
        int coefsCount = 0;
        double freeTerm = 0d;

        for (String atom : atoms) {
            Matcher m = p.matcher(atom);
            if (coefsCount != atoms.length - 1) {
                if (m.find())
                    coefs[coefsCount] = Double.parseDouble(m.group(0));
                coefsCount++;
            } else {
                if (m.find())
                    freeTerm = Double.parseDouble(m.group(0));
            }
        }

        return new Limitation(coefs, sign, freeTerm);
    }

/*
	public static String perform(String inputFileName, boolean verbose, boolean integer, boolean csv, boolean minimize) {
		try {
            SimplexTable simplexTable = new SimplexTable(createSimplexTable(inputFileName, minimize));

            String states = verbose ? highlight("Исходная таблица: ") + "\n" + simplexTable + "\n" : "";
            Answer answer = solve(simplexTable);

            if (verbose) {
                ArrayList<SimplexTable> stateList = simplexTable.getStateList();

                int stateNumber = 1;
                for (SimplexTable state : stateList) {
                    states += highlight("Шаг " + stateNumber) + "\n" + state + "\n";
                    stateNumber++;
                }
            }

            String prefix = csv ? ""
                    : ( highlight("Задача") + "\n" +
                        simplexTable.problemString() + "\n" +
                        states +
                        highlight("Решение"));

	       	return prefix + answer.toString(integer, csv);
		} catch (IOException e) {
			return "Файл не найден: " + inputFileName;
		}
	}

    public static String perform(String inputFileName) {
        return perform(inputFileName, false, false, false, false);
    }

    public static Answer solve(SimplexTable simplexTable) {
        int cols = simplexTable.cols();
        int rows = simplexTable.rows();
        int resCol, resRow;
        int [] rowNames = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;
        Answer answer = new Answer();

        for (int j = 0; j < rows; j++)
            rowNames[j] = cols + j + 1;

        while (!solved) {
            resCol = simplexTable.findResCol();
            if (resCol == -1) {
                return new NullAnswer();
            } else {
                solved = resCol == 0;

                if (!solved) {
                    resRow = simplexTable.findResRow(resCol);
                    rowNames[resRow] = resCol;
                    simplexTable.step(resRow, resCol);
                }
            }
        }

        for (int i = 1; i <= cols; i++) {
            int j = 0;
            for (; j < rows && rowNames[j] != i; j++);
            if (j == rows)
                answer.addItem("x" + i, 0);
            else
                answer.addItem("x" + i, simplexTable.getElement(j, 0));
        }

        answer.addItem("max{ F(x) }", simplexTable.getElement(rows, 0));

		return answer;
    }

    public static double[][] createSimplexTable(String fileName, boolean minimize) throws IOException {

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
            simplexTable[rows][i] = minimize ? scanner.nextDouble() : -scanner.nextDouble();
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

    public static double[][] createSimplexTable(String fileName) throws IOException {
        return createSimplexTable(fileName, false);
    }

    private static String highlight(String line) {
        return "\033[7m" + line + "\033[0m";
    }
*/
}
