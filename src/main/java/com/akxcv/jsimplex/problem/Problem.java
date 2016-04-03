package com.akxcv.jsimplex.problem;

import com.akxcv.jsimplex.exception.FunctionNotLimitedException;

/**
 * Created by evgeny on 02.04.16.
 */
public class Problem {

    private SimplexTable simplexTable;

    public Problem(SimplexTable simplexTable) {
        this.simplexTable = simplexTable;
    }

    public Answer solve() throws FunctionNotLimitedException {
        int col, resRow, resCol;
        boolean solved = false, firstStepIsDone = false;
        int rows = simplexTable.rows();
        int cols = simplexTable.cols();
        int[] rowId = new int[rows - 1];
        int[] colId = new int[cols - 1];

        for (int i = 0; i < cols - 1; ++i) { colId[i] = i + 1; }
        for (int i = 0; i < rows - 1; ++i) { rowId[i] = i + cols; }

        while (!solved) {
            if(!firstStepIsDone) {
                col = simplexTable.findCol();
                if (col == 0) {
                    firstStepIsDone = true;
                } else {
                    resRow = simplexTable.findResRow(col);
                    if (resRow == -1) {
                        throw new FunctionNotLimitedException("Решений нет");
                    } else {
                        resCol = simplexTable.findResCol(resRow);
                        rowId[resRow] += colId[resCol - 1];
                        colId[resCol - 1] = rowId[resRow] - colId[resCol - 1];
                        rowId[resRow] -= colId[resCol - 1];
                        simplexTable.step(resRow, resCol);
                    }
                }
            } else {
                resRow = simplexTable.findResRow();
                if (resRow == -1) {
                    solved = true;
                } else {
                    resCol = simplexTable.findResColModifited(resRow);
                    if (resCol == -1) {
                        throw new FunctionNotLimitedException("Решений нет");
                    } else {
                        rowId[resRow] += colId[resCol - 1];
                        colId[resCol - 1] = rowId[resRow] - colId[resCol - 1];
                        rowId[resRow] -= colId[resCol - 1];
                        simplexTable.step(resRow, resCol);
                    }
                }
            }
        }

        System.out.println("ANS: \n" + simplexTable);
        return new Answer(null);
    }

}
