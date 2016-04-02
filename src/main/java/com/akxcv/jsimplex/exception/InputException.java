package com.akxcv.jsimplex.exception;

/**
 * Created by ak on 02.04.16.
 */
public class InputException extends Exception {

    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
