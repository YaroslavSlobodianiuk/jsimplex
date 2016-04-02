package com.github.akxcv.jsimplex;

import java.util.Map;
import java.util.LinkedHashMap;

public class Answer {

    private LinkedHashMap<String, Double> items;

    public Answer() {
        items = new LinkedHashMap();
    }

    public void addItem(String key, double value) {
        items.put(key, value);
    }

    public String toString(boolean integer, boolean asCsv) {
        String string = "";

        for (Map.Entry<String, Double> item : items.entrySet()) {
            string += "\n" + item.getKey() + (asCsv ? ", " : " = ");
            if (integer)
                string += (int) Math.floor(item.getValue());
            else
                string += item.getValue();
        }

        return string;
    }

    public String toString() {
        return toString(false, false);
    }

}
