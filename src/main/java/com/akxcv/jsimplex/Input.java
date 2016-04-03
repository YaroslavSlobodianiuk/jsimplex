package com.akxcv.jsimplex;

import com.akxcv.jsimplex.problem.CostFunction;
import com.akxcv.jsimplex.problem.Limitation;

/**
 * Created by ak on 02.04.16.
 */
class Input {

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

    public int getLimitationCount() {
        return limitations.length;
    }

}
