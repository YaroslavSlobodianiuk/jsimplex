package com.akxcv.jsimplex.problem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

public class Answer {

    private LinkedHashMap<String, Double> items;
    private SimplexTable simplexTable;

    Answer(SimplexTable simplexTable) {
        this.simplexTable = simplexTable;
        items = new LinkedHashMap<>();
    }

    protected void addItem(String key, double value) {
        items.put(key, value);
    }

    public String toString(boolean verbose, boolean integer, boolean asCsv) {
        String prefix = "";
        String string = "";

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("0.####");

        if (verbose) {
            int step = 1;
            for (SimplexTable state: simplexTable.getStateList()) {
                prefix += "Step " + step + "\n";
                prefix += state + "\n";
                step++;
            }
        }

        for (Map.Entry<String, Double> item : items.entrySet()) {
            string += "\n" + item.getKey() + (asCsv ? ", " : " = ");
            if (integer)
                string += (int) Math.floor(item.getValue());
            else
                string += df.format(item.getValue());
        }

        return prefix + string.substring(1);
    }

    public String toString() {
        return toString(false, false, false);
    }

}
