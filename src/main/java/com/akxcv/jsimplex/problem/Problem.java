package com.akxcv.jsimplex.problem;

/**
 * Created by evgeny on 02.04.16.
 */
public class Problem {
    private SimplexTable simplexTable;

    public Problem(SimplexTable simplexTable) {
        this.simplexTable = simplexTable;
    }

    public Answer solve() {
        int cols = simplexTable.cols();
        int rows = simplexTable.rows();
        int resCol, resRow;
        int [] rowNames = new int[rows];
        boolean solved = false;
        Answer answer = new Answer();

        for (int j = 0; j < rows; j++)
            rowNames[j] = cols + j + 1;

        while (!solved) {
            resCol = simplexTable.findResCol();
            if (resCol == -1) {
                return new NullAnswer();
            } else {
                solved = resCol == 0;

                if (!solved) {
                    resRow = simplexTable.findResRow(resCol);
                    rowNames[resRow] = resCol;
                    simplexTable.step(resRow, resCol);
                }
            }
        }

        for (int i = 1; i <= cols; i++) {
            int j = 0;
            for (; j < rows && rowNames[j] != i; j++);
            if (j == rows)
                answer.addItem("x" + i, 0);
            else
                answer.addItem("x" + i, simplexTable.getElement(j, 0));
        }

        answer.addItem("max{ F(x) }", simplexTable.getElement(rows, 0));

        return answer;
    }
}
