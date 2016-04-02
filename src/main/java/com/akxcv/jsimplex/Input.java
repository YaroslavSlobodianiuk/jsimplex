package com.akxcv.jsimplex;

/**
 * Created by ak on 02.04.16.
 */
class Input {

    private CostFunction costFunction;
    private Limitation[] limitations;

    Input(CostFunction costFunction, Limitation[] limitation) {
        this.costFunction = costFunction;
        this.limitations = limitation;
    }

    CostFunction getCostFunction() {
        return costFunction;
    }

    Limitation[] getLimitations() {
        return limitations;
    }

    int getLimitationCount() {
        return limitations.length;
    }

}
