package com.akxcv.jsimplex.problem;

import com.akxcv.jsimplex.exception.NoSolutionException;

/**
 * Created by evgeny on 02.04.16.
 */
public class Problem {

    private SimplexTable simplexTable;
    private CostFunction costFunction;
    private Limitation[] limitations;
    private int[] rowId;
    private int[] colId;
    private boolean isFirstStepDone;

    public Problem(SimplexTable simplexTable, CostFunction costFunction, Limitation[] limitations) {
        this.simplexTable = simplexTable;
        this.costFunction = costFunction;
        this.limitations = limitations;
        setup();
    }

    private void setup() {
        this.rowId = new int[simplexTable.rows() - 1];
        this.colId = new int[simplexTable.cols() - 1];
        this.isFirstStepDone = false;
    }

    public Answer solve() throws NoSolutionException {
        boolean solved = false;

        for (int i = 0; i < simplexTable.cols() - 1; ++i) { colId[i] = i + 1; }
        for (int i = 0; i < simplexTable.rows() - 1; ++i) { rowId[i] = i + simplexTable.cols(); }

        while (!solved) {
            if(!isFirstStepDone) {
                solveFirstStep();
            } else {
                solved = solveLastStep();
            }
        }

        return createAnswer();
    }

    private void solveFirstStep() throws NoSolutionException {
        int col = simplexTable.findColWithNegativeElement();
        if (col == 0) {
            isFirstStepDone = true;
        } else {
            int resRow = simplexTable.findResRow(col);
            if (resRow == -1) {
                throw new NoSolutionException("Решения нет");
            } else {
                int resCol = simplexTable.findResCol(resRow, true);
                rowId[resRow] += colId[resCol - 1];
                colId[resCol - 1] = rowId[resRow] - colId[resCol - 1];
                rowId[resRow] -= colId[resCol - 1];
                simplexTable.step(resRow, resCol);
            }
        }
    }

    private boolean solveLastStep() throws NoSolutionException {
        int resRow = simplexTable.findResRow();
        if (resRow == -1) {
            return true;
        } else {
            int resCol = simplexTable.findResCol(resRow, false);
            if (resCol == -1) {
                throw new NoSolutionException("Решения нет");
            } else {
                rowId[resRow] += colId[resCol - 1];
                colId[resCol - 1] = rowId[resRow] - colId[resCol - 1];
                rowId[resRow] -= colId[resCol - 1];
                simplexTable.step(resRow, resCol);
            }
        }
        return false;
    }

    private Answer createAnswer() {
        Answer answer = new Answer(simplexTable);

        for (int i = 1; i < simplexTable.cols(); i++) {
            int j = 0;
            for (; j < simplexTable.rows() - 1 && rowId[j] != i; j++);
            if (j == simplexTable.rows() - 1)
                answer.addItem("x" + i, 0);
            else
                answer.addItem("x" + i, simplexTable.getElement(j, 0));
        }

        String optimizationDirection = costFunction.shouldBeMinimized() ? "min" : "max";
        double costFunctionValue = (costFunction.shouldBeMinimized() ? -1 : 1) * simplexTable.getElement(simplexTable.rows() - 1, 0);
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
