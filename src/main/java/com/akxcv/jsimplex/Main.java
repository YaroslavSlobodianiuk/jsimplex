package com.akxcv.jsimplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.akxcv.jsimplex.exception.NoSolutionException;
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
        Locale.setDefault(Locale.US);
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
        if (!options.containsKey("output") || ( options.containsKey("verbose") && options.get("verbose") == true ))
            System.out.println(problem);

        try {
            produceOutput(problem.solve(), options);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            if (options.containsKey("debug") && options.get("debug").equals(true))
                e.printStackTrace();
        } catch (NoSolutionException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Problem createProblem(Input input) {
        LinkedHashSet<Variable> variables = mergeVariables(input);
        int rows = input.getLimitationCount() + 1;
        int cols = variables.size() + 1;
        double [][] table = new double[rows][cols];

        table[rows - 1][0] = 0;

        int limitationIndex = 0, variableIndex = 0;
        for (Variable v : variables) {
            if (input.getCostFunction().shouldBeMinimized())
                table[rows - 1][variableIndex + 1] = input.getCostFunction().getCoef(v);
            else
                table[rows - 1][variableIndex + 1] = -input.getCostFunction().getCoef(v);
            variableIndex++;
        }

        for (Limitation l : input.getLimitations()) {
            variableIndex = 1;
            for (Variable v : variables) {
                if (l.getSign() == Limitation.LimitationSign.GE)
                    table[limitationIndex][variableIndex] = -input.getLimitation(limitationIndex).getCoef(v);
                else
                    table[limitationIndex][variableIndex] = input.getLimitation(limitationIndex).getCoef(v);
                variableIndex++;
            }
            limitationIndex++;
        }

        limitationIndex = 0;
        for (Limitation l : input.getLimitations()) {
            if (l.getSign() == Limitation.LimitationSign.GE)
                table[limitationIndex][0] = -l.getFreeTerm();
            else
                table[limitationIndex][0] = l.getFreeTerm();
            limitationIndex++;
        }

        return new Problem(new SimplexTable(table), input.getCostFunction(), input.getLimitations());
    }

    private static LinkedHashSet<Variable> mergeVariables(Input input) {
        LinkedHashSet<Variable> variables = new LinkedHashSet<>(Arrays.asList(input.getCostFunction().getVariables()));

        for (Limitation l : input.getLimitations())
            for (Variable v : l.getVariables())
                variables.add(v);

        return variables;
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
        Scanner scanner = new Scanner(new File(fileName));

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
        Scanner scanner = new Scanner(System.in);

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

    private static CostFunction stringToCostFunction(String input) throws InputException {
        input = input.replaceAll("\\s", "");

        if (!input.contains("min") && !input.contains("max"))
            throw new InputException("Не задано направление оптимизации");
        if (input.contains("min") && input.contains("max"))
            throw new InputException("Задано несколько направлений оптимизации");
        boolean shouldBeMinimized = input.contains("min");

        input = input.replaceAll("(-?->)?(max|min)", "");
        ArrayList<String> atoms = new ArrayList<>(Arrays.asList(input.split("(?=\\+)|(?=\\-)")));

        HashMap<String, Object> parseResult = parseAtoms(atoms);

        return new CostFunction((double[]) parseResult.get("coefs"), (Variable[]) parseResult.get("vars"), shouldBeMinimized);
    }

    private static Limitation stringToLimitation(String input) throws InputException {
        Limitation.LimitationSign sign;

        if (input.contains("<=")) {
            if (input.contains(">="))
                throw new InputException("Неверный знак ограничения");
            sign = Limitation.LimitationSign.LE;
        } else if (input.contains(">=")) {
            sign = Limitation.LimitationSign.GE;
        } else
            sign = Limitation.LimitationSign.EQ;

        if (!input.contains("="))
            throw new InputException("Не указан знак ограничения");

        input = input.replaceAll("\\s|<|>", "");

        ArrayList<String> atoms = new ArrayList<>(Arrays.asList(input.split("=|(?=\\+)|(?=\\-)")));
        atoms.removeAll(Arrays.asList("", null));
        double freeTerm = Double.parseDouble(atoms.remove(atoms.size() - 1));

        HashMap<String, Object> parseResult = parseAtoms(atoms);

        return new Limitation((double[]) parseResult.get("coefs"), (Variable[]) parseResult.get("vars"), sign, freeTerm);
    }

    private static HashMap<String, Object> parseAtoms(ArrayList<String> atoms) throws InputException {
        int coefsCount = 0;
        Pattern p = Pattern.compile("((?:[\\-\\+])?\\d*(?:\\.\\d+)?)?([a-zA-Z]+)?(\\d*)?");
        double[] coefs = new double[atoms.size()];
        Variable[] variables = new Variable[atoms.size()];

        for (String atom : atoms) {
            Matcher m = p.matcher(atom);
            if (m.find()) {
                if (m.group(1).equals("") || m.group(1).equals("+"))
                    coefs[coefsCount] = 1;
                else if (m.group(1).equals("-"))
                    coefs[coefsCount] = -1;
                else
                    coefs[coefsCount] = Double.parseDouble(m.group(1));
                if (m.group(3).equals(""))
                    variables[coefsCount] = new Variable(m.group(2));
                else
                    variables[coefsCount] = new Variable(m.group(2), Integer.parseInt(m.group(3)));
            } else throw new InputException("Неверно введены переменные");

            coefsCount++;
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("coefs", coefs);
        result.put("vars", variables);
        return result;
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
