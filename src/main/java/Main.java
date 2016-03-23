import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;

public class Main {

    public static boolean check_limitation(int col, int rows, final double [][] simplex_table) {
        for (int i = 0; i < rows; ++i)
            if (simplex_table[i][col] > 0.0)
                return true;
        return false;
    }

    public static int find_resCol(int rows, int cols, final double [][] simplex_table) {
        int resCol = 0;
        for (int i = 1; i <= cols; ++i)
            if (simplex_table[rows][i] < 0.0) {
                if (check_limitation(i, rows, simplex_table)) {
                    if (resCol == 0 || simplex_table[rows][i] < simplex_table[rows][resCol])
                        resCol = i;
                }
                else
                    return -1;
            }
        return resCol;
    }

    public static int find_resRow(int rows, int resCol, final double [][] simplex_table) {
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

    public static void make_step(int rows, int cols, int resRow, int resCol, double [][] simplex_table) {
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

    public static String solve(int rows, int cols, double[][] simplex_table) {
        int resCol, resRow;
        int [] rows_names = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;
		String sln = "";

        for (int j = 0; j < rows; ++j)
            rows_names[j] = cols + j + 1;

        while (!solved) {
            resCol = find_resCol(rows, cols, simplex_table);
            if (resCol == -1) {
                // System.out.print("Objective function is unlimited\n");
				return "Objective function is unlimited\n";
            } else if (resCol == 0) {
                solved = true;
            } else {
                resRow = find_resRow(rows, resCol, simplex_table);
                rows_names[resRow] = resCol;
                make_step(rows, cols, resRow, resCol, simplex_table);
            }
        }

        for (int i = 1; i <= cols; ++i) {
            int j = 0;
            for (; j < rows && rows_names[j] != i; ++j);
			sln += "x" + i + " = ";
            if (j == rows) {
				sln += "0\n";
            }
            else {
				sln += simplex_table[j][0] + "\n";
            }
        }
		sln += "F(x) = " + simplex_table[rows][0];
		return sln;
    }
	
	public static String perform(String inputFileName) {
		try {
			Scanner scanner = new Scanner(new File(inputFileName));
		   	int rows, cols;

	       	cols = scanner.nextInt();
	       	rows = scanner.nextInt();

	       	double [][] simplex_table = new double[rows + 1][cols + 1];

	       	simplex_table[rows][0] = 0;
	       	for (int i = 1; i <= cols; ++i)
	    	   simplex_table[rows][i] = -scanner.nextDouble();
	       	for (int i = 0; i < rows; ++i)
	    	   for (int j = 1; j <= cols; ++j)
	    		   simplex_table[i][j] = scanner.nextDouble();
	       	for (int i = 0; i < rows; ++i)
	        	simplex_table[i][0] = scanner.nextDouble();

	       	return solve(rows, cols, simplex_table);
		} catch (IOException e) {
			return "File not found: " + inputFileName;
		}
	}

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
		
		// Имя входного файла
		String inputFileName;
		if (line.hasOption("input"))
			inputFileName = line.getOptionValue("input");
		else
			inputFileName = "test.txt";
		
		String output = perform(inputFileName);
      
	  	// Вывод
		if (line.hasOption("output")) {
			try( PrintWriter out = new PrintWriter(line.getOptionValue("output")) ) {
				out.println(output);
			}
		} else {
			System.out.println(output);
		}
    }
}
