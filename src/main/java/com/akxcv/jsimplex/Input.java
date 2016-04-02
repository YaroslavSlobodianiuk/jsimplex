package com.akxcv.jsimplex;

/**
 * Created by ak on 02.04.16.
 */
public class Input {

    private CostFunction costFunction;
    private Limitation[] limitations;

    public Input(CostFunction costFunction, Limitation[] limitation) {
        this.costFunction = costFunction;
        this.limitations = limitation;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public Limitation[] getLimitations() {
        return limitations;
    }

}
