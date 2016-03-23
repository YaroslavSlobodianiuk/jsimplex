import java.io.File;
import java.io.IOException;
import java.util.Scanner;

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

    public static void solve(int rows, int cols, double[][] simplex_table) {
        int resCol, resRow;
        int [] rows_names = new int[rows];
        double [] solution = new double[rows];
        boolean solved = false;

        for (int j = 0; j < rows; ++j)
            rows_names[j] = cols + j + 1;

        while (!solved) {
            resCol = find_resCol(rows, cols, simplex_table);
            if (resCol == -1) {
                System.out.print("Objective function is unlimited\n");
                return;
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
            System.out.print("x");
            System.out.print(i);
            System.out.print(" = ");
            if (j == rows) {
                System.out.println(0);
            }
            else {
                System.out.println(simplex_table[j][0]);
            }
        }
        System.out.print("F(x) = ");
        System.out.print(simplex_table[rows][0]);
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new File("test1.txt"));
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

        solve(rows, cols, simplex_table);
    }
}
















