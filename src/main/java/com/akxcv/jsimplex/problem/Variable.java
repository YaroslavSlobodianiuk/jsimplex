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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (index != variable.index) return false;
        return letter.equals(variable.letter);

    }

    @Override
    public int hashCode() {
        int result = letter.hashCode();
        result = 31 * result + index;
        return result;
    }
}
