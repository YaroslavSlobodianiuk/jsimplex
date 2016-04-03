package com.akxcv.jsimplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.akxcv.jsimplex.exception.FunctionNotLimitedException;
import com.akxcv.jsimplex.exception.InputException;
import com.akxcv.jsimplex.problem.*;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) {
        HashMap options;

        try {
            options = parseCommandLine(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        Input input;
        try {
            input = getUserInput(options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (options.containsKey("debug") && options.get("debug").equals(true))
                e.printStackTrace();
            return;
        }

        Problem problem = createProblem(input);
        System.out.println(problem);

        try {
            produceOutput(problem.solve(), options);
        } catch (FileNotFoundException e) {
           System.out.println(e.getMessage());
           if (options.containsKey("debug") && options.get("debug").equals(true))
               e.printStackTrace();
        }  catch (FunctionNotLimitedException e) {
           System.out.println(e.getMessage());
        }
    }

    private static Problem createProblem(Input input) {
        int rows = input.getLimitationCount() + 1;
        int cols = input.getCostFunction().getCoefCount() + 1;
        double [][] table = new double[rows][cols];

        table[rows - 1][0] = 0;
        for (int i = 1; i < cols; ++i) {
            if (input.getCostFunction().shouldBeMinimized())
                table[rows - 1][i] = input.getCostFunction().getCoef(i - 1);
            else
                table[rows - 1][i] = -input.getCostFunction().getCoef(i - 1);
        }

        for (int i = 0; i < rows - 1; ++i) {
            for (int j = 1; j < cols; ++j)
                if (input.getLimitations()[i].getSign() == Limitation.LimitationSign.GE)
                    table[i][j] = -input.getLimitations()[i].getCoef(j - 1);
                else
                    table[i][j] = input.getLimitations()[i].getCoef(j - 1);
        }

        for (int i = 0; i < rows - 1; ++i) {
            if (input.getLimitations()[i].getSign() == Limitation.LimitationSign.GE)
                table[i][0] = -input.getLimitations()[i].getFreeTerm();
            else
                table[i][0] = input.getLimitations()[i].getFreeTerm();
        }

        return new Problem(new SimplexTable(table), input.getCostFunction(), input.getLimitations());
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

    private static Input getUserInput(HashMap options) throws FileNotFoundException, InputException {
        if (options.containsKey("input")) {
            return getUserFileInput(options.get("input").toString());
        } else {
            return getUserKeyboardInput();
        }
    }

    private static Input getUserFileInput(String fileName) throws InputException, FileNotFoundException {
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

    private static Input getUserKeyboardInput() throws InputException {
        Scanner scanner = new Scanner(System.in).useLocale(new Locale("US"));

        String line;
        CostFunction costFunction;
        ArrayList<Limitation> limitations = new ArrayList<>();

        System.out.println("Введите условие задачи. Для окончания ввода нажмите Enter в пустой строке.");
        System.out.print("Целевая функция: ");
        line = scanner.nextLine();

        if (line.isEmpty()) {
            throw new InputException("Пустой ввод");
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

    private static CostFunction stringToCostFunction(String string) throws InputException {
        string = string.replaceAll("\\s", "");

        if (!string.contains("->") || !(string.contains("min") || string.contains("max")))
            throw new InputException("Не задано направление оптимизации");

        ArrayList<String> atomsList = new ArrayList<>(Arrays.asList(string.split("\\-\\->|\\->|(?=\\+)|(?=\\-)")));
        atomsList.removeAll(Arrays.asList("", null));
        String[] atoms = atomsList.toArray(new String[0]);

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

    private static Limitation stringToLimitation(String string) throws InputException {
        Limitation.LimitationSign sign;

        if (string.contains("<=")) {
            if (string.contains(">="))
                throw new InputException("Неверный знак ограничения");
            sign = Limitation.LimitationSign.LE;
        } else if (string.contains(">=")) {
            sign = Limitation.LimitationSign.GE;
        } else
            sign = Limitation.LimitationSign.EQ;

        if (!string.contains("="))
            throw new InputException("Не указан знак ограничения");

        string = string.replaceAll("\\s|<|>", "");

        ArrayList<String> atomsList = new ArrayList<>(Arrays.asList(string.split("=|(?=\\+)|(?=\\-)")));
        atomsList.removeAll(Arrays.asList("", null));
        String[] atoms = atomsList.toArray(new String[0]);

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

    private static void produceOutput(Answer answer, HashMap options) throws FileNotFoundException {
        if (options.containsKey("output"))
            writeOutputToFile(answer, options);
        else
            System.out.println(answer.toString((boolean) options.get("verbose"), (boolean) options.get("integer"), (boolean) options.get("csv")));
    }

    private static void writeOutputToFile(Answer answer, HashMap options) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(options.get("output").toString());
        out.println(answer.toString((boolean) options.get("verbose"), (boolean) options.get("integer"), (boolean) options.get("csv")));
        out.close();
    }

}
