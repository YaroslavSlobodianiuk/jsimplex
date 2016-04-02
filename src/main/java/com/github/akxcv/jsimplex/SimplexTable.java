package com.github.akxcv.jsimplex;

import java.util.ArrayList;

public class SimplexTable {

    private double[][] table;
    private ArrayList<SimplexTable> stateList;

    public SimplexTable(double[][] table) {
        this.table = table;
        stateList = new ArrayList();
    }

    public int rows() {
        return table.length - 1;
    }

    public int cols() {
        return table[0].length - 1;
    }

    public double getElement(int row, int column) {
        return table[row][column];
    }

    public int findResCol() {
        int resCol = 0;

        for (int i = 1; i <= cols(); i++)
            if (table[rows()][i] < 0f) {
                if (isLimited(i)) {
                    if (resCol == 0 || table[rows()][i] < table[rows()][resCol])
                        resCol = i;
                }
                else
                    return -1;
            }

        return resCol;
    }

    public int findResRow(int resCol) {
        if (resCol < 0)
            return -1;

        double minRatio = -1f, ratio = 0f;
        int resRow = 0;

        for (int i = 0; i < rows(); i++)
            if (table[i][resCol] > 0f) {
                ratio = table[i][0] / table[i][resCol];
                if (minRatio < 0 || minRatio > ratio) {
                    minRatio = ratio;
                    resRow = i;
                }
            }

        return resRow;
    }

    public void step(int resRow, int resCol) {
        stateList.add(new SimplexTable(table));

        for (int i = 0; i <= rows(); i++)
            if (i != resRow)
                for (int j = 0; j <= cols(); j++)
                    if (j != resCol)
                        table[i][j] -= table[resRow][j] * table[i][resCol] / table[resRow][resCol];

        for (int j = 0; j <= cols(); j++)
            if (j != resCol)
                table[resRow][j] /= table[resRow][resCol];

        for (int i = 0; i <= rows(); ++i)
            if (i != resRow)
                table[i][resCol] /= -table[resRow][resCol];

        table[resRow][resCol] = 1 / table[resRow][resCol];
    }

    public String problemString() {
        String problem = "\033[1mF(x) = " + Double.toString(-table[rows()][1]) + " * x1";

        for (int i = 2; i <= cols(); i++) {
            if (Math.signum(-table[rows()][i]) < 0) {
                problem += " - ";
            }
            else {
                problem += " + ";
            }
            problem += Double.toString(Math.abs(table[rows()][i])) + " * x" + Integer.toString(i);
        }

        problem += " --> max\033[0m\n";

        for (int i = 0; i < rows(); i++) {
            problem += Double.toString(table[i][1]) + " * x1";
            for (int j = 2; j <= cols(); j++) {
                if (Math.signum(table[i][j]) < 0) {
                    problem += " - ";
                }
                else {
                    problem += " + ";
                }
                problem += Double.toString(Math.abs(table[i][j])) + " * x" + Integer.toString(j);
            }
            problem += " <= " + Double.toString(table[i][0]) + '\n';
        }
        problem += "x_i >= 0, i = 1.." + Integer.toString(cols()) + "\033[0m\n";

        return problem;
    }

    public ArrayList<SimplexTable> getStateList() {
        return stateList;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean shouldFindResElement) {
        String string = "";

        for (int i = 0; i <= rows(); i++) {

            for (int j = 0; j <= cols(); j++) {

                if (shouldFindResElement) {
                    int resCol = findResCol();
                    int resRow = findResRow(resCol);

                    if (i == resRow && j == resCol)
                        string += "\033[4m\033[1m" + getElement(i, j) + "\033[0m";
                    else
                        string += getElement(i, j);
                } else
                    string += getElement(i, j);

                string += " ";
            }

            string += '\n';
        }

        return string;
    }

    private boolean isLimited(int column) {
        for (int i = 0; i < rows(); i++)
            if (table[i][column] > 0f)
                return true;

        return false;
    }

}
