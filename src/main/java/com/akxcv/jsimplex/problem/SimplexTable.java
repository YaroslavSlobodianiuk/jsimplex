package com.akxcv.jsimplex.problem;

import java.util.ArrayList;

public class SimplexTable {

    private double[][] table;
    private ArrayList<SimplexTable> stateList;

    public SimplexTable(double[][] table) {
        this.table = table;
        stateList = new ArrayList<>();
    }

    protected int rows() {
        return table.length;
    }

    protected int cols() {
        return table[0].length;
    }

    protected double getElement(int row, int column) {
        return table[row][column];
    }

    protected int findCol() {
        for (int i = 1; i < cols(); i++)
            if (table[rows() - 1][i] < 0)
                return i;
        return 0;
    }

    protected int findResCol(int resRow) {
        double r, maxR = 0;
        boolean firstRatio = true;
        int resCol = 0;

        for (int i = 1; i < cols(); ++i) {
            if (table[resRow][i] != 0) {
                r = table[rows() - 1][i] / table[resRow][i];
                if ( r <= 0 && (firstRatio || r > maxR) ) {
                    firstRatio = false;
                    maxR = r;
                    resCol = i;
                }
            }
        }

        return resCol;
    }

    protected int findResRow(int col) {
        for (int i = 0; i < rows() - 1; i++) {
            if (table[i][col] > 0)
                return i;
        }
        return -1;
    }

    protected int findResRow() {
        for (int i = 0; i < rows() - 1; ++i) {
            if (table[i][0] < 0)
                return i;
        }
        return -1;
    }

    protected int findResColModifited(int resRow) {
        double r, maxR = 0;
        boolean firstRatio = true;
        int resCol = -1;

        for (int i = 1; i < cols(); ++i) {
            if (table[resRow][i] < 0) {
                r = table[rows() - 1][i] / table[resRow][i];
                if ( (r <= 0) && (firstRatio || r > maxR) ) {
                    firstRatio = false;
                    maxR = r;
                    resCol = i;
                }
            }
        }

        return resCol;
    }

    protected void step(int resRow, int resCol) {
        stateList.add(new SimplexTable(table));

        for (int i = 0; i < rows(); i++)
            if (i != resRow)
                for (int j = 0; j < cols(); j++)
                    if (j != resCol)
                        table[i][j] -= table[resRow][j] * table[i][resCol] / table[resRow][resCol];

        for (int j = 0; j < cols(); j++)
            if (j != resCol)
                table[resRow][j] /= table[resRow][resCol];

        for (int i = 0; i < rows(); ++i)
            if (i != resRow)
                table[i][resCol] /= -table[resRow][resCol];

        table[resRow][resCol] = 1 / table[resRow][resCol];

        /*
        for (int i = 0; i < rows; ++i)
        if (i != resRow)
            for (int j = 0; j < cols; ++j)
                if (j != resCol)
                    simplexTable[i][j] -= simplexTable[resRow][j] * simplexTable[i][resCol] / simplexTable[resRow][resCol];

    for (int j = 0; j < cols; ++j)
        if (j != resCol)
            simplexTable[resRow][j] /= simplexTable[resRow][resCol];

    for (int i = 0; i < rows; ++i)
        if (i != resRow)
            simplexTable[i][resCol] /= -simplexTable[resRow][resCol];

    simplexTable[resRow][resCol] = 1 / simplexTable[resRow][resCol];
         */
    }

    protected ArrayList<SimplexTable> getStateList() {
        return stateList;
    }

    public String toString() {
        return toString(true);
    }

    protected String toString(boolean shouldFindResElement) {
        String string = "";

        for (int i = 0; i < rows(); i++) {

            for (int j = 0; j < cols(); j++) {

                if (shouldFindResElement) {
                    int resCol = findCol();
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
