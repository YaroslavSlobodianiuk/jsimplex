import java.io.File;
import java.lang.Integer;
import java.util.Scanner;
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
            int rows, cols;
            double [][] simplex_table;

            if (inputFileName == null) {
                simplex_table = commandLineInput();
                cols = simplex_table[0].length - 1;
                rows = simplex_table.length - 1;
            } else {
                Scanner scanner = new Scanner(new File(inputFileName));

    	       	cols = scanner.nextInt();
    	       	rows = scanner.nextInt();

    	       	simplex_table = new double[rows + 1][cols + 1];

    	       	simplex_table[rows][0] = 0;
    	       	for (int i = 1; i <= cols; ++i)
    	    	   simplex_table[rows][i] = -scanner.nextDouble();
    	       	for (int i = 0; i < rows; ++i)
    	    	   for (int j = 1; j <= cols; ++j)
    	    		   simplex_table[i][j] = scanner.nextDouble();
    	       	for (int i = 0; i < rows; ++i)
    	        	simplex_table[i][0] = scanner.nextDouble();
            }

	       	return problem(rows, cols, simplex_table) + solve(rows, cols, simplex_table);
		} catch (IOException e) {
			return "File not found: " + inputFileName;
		}
	}

    private static String problem(int rows, int cols, double[][] simplex_table) {
        String _problem = "Problem:\n\nF(x) = " + Double.toString(-simplex_table[rows][1]) + " * x1";
        for (int i = 2; i <= cols; ++i) {
            if (Math.signum(-simplex_table[rows][i]) < 0) {
                _problem += " - ";
            }
            else {
                _problem += " + ";
            }
            _problem += Double.toString(Math.abs(simplex_table[rows][i])) + " * x" + Integer.toString(i);
        }
        _problem += " --> max\n";
        for (int i = 0; i < rows; ++i) {
            _problem += Double.toString(simplex_table[i][1]) + " * x1";
            for (int j = 2; j <= cols; ++j) {
                if (Math.signum(simplex_table[i][j]) < 0) {
                    _problem += " - ";
                }
                else {
                    _problem += " + ";
                }
                _problem += Double.toString(Math.abs(simplex_table[i][j])) + " * x" + Integer.toString(j);
            }
            _problem += " <= " + Double.toString(simplex_table[i][0]) + '\n';
        }
        _problem += "x_i >= 0, i = 1 to " + Integer.toString(cols) + "\n";
        _problem += "\nSolution:\n\n";

        return _problem;
    }

    private static String solve(int rows, int cols, double[][] simplex_table) {
        int resCol, resRow;
        int [] rows_names = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;
		String answer = "";

        for (int j = 0; j < rows; ++j)
            rows_names[j] = cols + j + 1;

        while (!solved) {
            resCol = findResCol(rows, cols, simplex_table);
            if (resCol == -1) {
				return "Objective function is unlimited\n";
            } else if (resCol == 0) {
                solved = true;
            } else {
                resRow = findResRow(rows, resCol, simplex_table);
                rows_names[resRow] = resCol;
                makeStep(rows, cols, resRow, resCol, simplex_table);
            }
        }

        for (int i = 1; i <= cols; ++i) {
            int j = 0;
            for (; j < rows && rows_names[j] != i; ++j);
			answer += "x" + i + " = ";
            if (j == rows) {
				answer += "0\n";
            }
            else {
				answer += simplex_table[j][0] + "\n";
            }
        }
		answer += "max{ F(x) } = " + simplex_table[rows][0] + '\n';
		return answer;
    }

    private static boolean checkLimitation(int col, int rows, final double [][] simplex_table) {
        for (int i = 0; i < rows; ++i)
            if (simplex_table[i][col] > 0.0)
                return true;
        return false;
    }

    private static int findResCol(int rows, int cols, final double [][] simplex_table) {
        int resCol = 0;
        for (int i = 1; i <= cols; ++i)
            if (simplex_table[rows][i] < 0.0) {
                if (checkLimitation(i, rows, simplex_table)) {
                    if (resCol == 0 || simplex_table[rows][i] < simplex_table[rows][resCol])
                        resCol = i;
                }
                else
                    return -1;
            }
        return resCol;
    }

    private static int findResRow(int rows, int resCol, final double [][] simplex_table) {
        double min_ratio = -1.0, ratio = 0.0;
        int resRow = 0;
        for (int i = 0; i < rows; ++i)
            if (simplex_table[i][resCol] > 0.0) {
                ratio = simplex_table[i][0] / simplex_table[i][resCol];
                if (min_ratio < 0 || min_ratio > ratio) {
                    min_ratio = ratio;
                    resRow = i;
                }
            }
        return resRow;
    }

    private static void makeStep(int rows, int cols, int resRow, int resCol, double [][] simplex_table) {
        for (int i = 0; i <= rows; ++i)
            if (i != resRow)
                for (int j = 0; j <= cols; ++j)
                    if (j != resCol)
                        simplex_table[i][j] -= simplex_table[resRow][j] * simplex_table[i][resCol] / simplex_table[resRow][resCol];

        for (int j = 0; j <= cols; ++j)
            if (j != resCol)
                simplex_table[resRow][j] /= simplex_table[resRow][resCol];

        for (int i = 0; i <= rows; ++i)
            if (i != resRow)
                simplex_table[i][resCol] /= -simplex_table[resRow][resCol];

        simplex_table[resRow][resCol] = 1 / simplex_table[resRow][resCol];
    }

    private static double[][] commandLineInput() {

        String input;
        Scanner keyboard = new Scanner(System.in);

        System.out.print("Number of variables: ");
        int cols = keyboard.nextInt();

        System.out.print("Number of limitations: ");
        int rows = keyboard.nextInt();

        double [][] simplex_table = new double[rows + 1][cols + 1];

        simplex_table[rows][0] = 0;
        System.out.println("\nCOST FUNCTION");
        for (int i = 1; i <= cols; ++i) {
            System.out.print("  Multiplier #" + i + ": ");
            simplex_table[rows][i] = -keyboard.nextDouble();
        }
        System.out.println("\nLIMITATIONS: MULTIPLIERS");
        for (int i = 0; i < rows; ++i) {
            System.out.println("  Limitation #" + (i + 1) + ":");
            for (int j = 1; j <= cols; ++j) {
                System.out.print("    Multiplier #" + j + ": ");
                simplex_table[i][j] = keyboard.nextDouble();
            }
        }
        System.out.println("\nLIMITATIONS: FREE TERMS");
        for (int i = 0; i < rows; ++i) {
            System.out.print(" Limitation #" + (i+1) + ": ");
            simplex_table[i][0] = keyboard.nextDouble();
        }

        System.out.println("");
        return simplex_table;

    }

}
