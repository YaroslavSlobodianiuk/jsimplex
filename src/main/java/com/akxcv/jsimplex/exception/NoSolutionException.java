package com.akxcv.jsimplex.exception;

/**
 * Created by ak on 02.04.16.
 */
public class NoSolutionException extends Exception {

    public NoSolutionException() {
        this("Cost function is not limited");
    }

    public NoSolutionException(String message) {
        super(message);
    }

    public NoSolutionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
