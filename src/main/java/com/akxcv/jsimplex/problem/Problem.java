package com.akxcv.jsimplex.problem;

import com.akxcv.jsimplex.exception.FunctionNotLimitedException;

/**
 * Created by evgeny on 02.04.16.
 */
public class Problem {

    private SimplexTable simplexTable;
    private CostFunction costFunction;
    private Limitation[] limitations;

    public Problem(SimplexTable simplexTable, CostFunction costFunction, Limitation[] limitations) {
        this.simplexTable = simplexTable;
        this.costFunction = costFunction;
        this.limitations = limitations;
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

        Answer answer = new Answer(simplexTable);

        for (int i = 1; i < cols; i++) {
            int j = 0;
            for (; j < rows - 1 && rowId[j] != i; j++);
            if (j == rows - 1)
                answer.addItem("x" + i, 0);
            else
                answer.addItem("x" + i, simplexTable.getElement(j, 0));
        }

        String optimizationDirection = costFunction.shouldBeMinimized() ? "min" : "max";
        double costFunctionValue = (costFunction.shouldBeMinimized() ? -1 : 1) * simplexTable.getElement(rows - 1, 0);
        answer.addItem(optimizationDirection + "{ F(x) }", costFunctionValue);

        return answer;
    }

    public String toString() {
        String result = "";

        result += costFunction + "\n";
        for (Limitation l : limitations)
            result += l + "\n";

        return result;
    }

}
