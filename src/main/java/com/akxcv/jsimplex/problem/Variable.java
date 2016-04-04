package com.akxcv.jsimplex.problem;

/**
 * Created by ak on 04.04.16.
 */
public class Variable {

    private String letter;
    private int index;

    public Variable(String letter, int index) {
        this.letter = letter;
        this.index = index;
    }

    public String toString() {
        return letter + index;
    }

    public String getLetter() {
        return letter;
    }

    public int getIndex() {
        return index;
    }

}
