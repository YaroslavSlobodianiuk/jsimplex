public class SimplexTable {

    private double[][] table;

    public SimplexTable(double[][] table) {
        this.table = table;
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

    public boolean isLimited(int column) {
        for (int i = 0; i < rows(); i++)
            if (table[i][column] > 0f)
                return true;

        return false;
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
        String problem = "Problem:\n\nF(x) = " + Double.toString(-table[rows()][1]) + " * x1";

        for (int i = 2; i <= cols(); i++) {
            if (Math.signum(-table[rows()][i]) < 0) {
                problem += " - ";
            }
            else {
                problem += " + ";
            }
            problem += Double.toString(Math.abs(table[rows()][i])) + " * x" + Integer.toString(i);
        }

        problem += " --> max\n";

        for (int i = 0; i < rows(); ++i) {
            problem += Double.toString(table[i][1]) + " * x1";
            for (int j = 2; j <= cols(); ++j) {
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
        problem += "x_i >= 0, i = 1.." + Integer.toString(cols()) + "\n";

        return problem;
    }

}
