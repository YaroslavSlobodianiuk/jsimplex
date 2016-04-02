package com.akxcv.jsimplex.exception;

/**
 * Created by ak on 02.04.16.
 */
public class LimitationException extends Exception {

    public LimitationException(String message) {
        super(message);
    }

    public LimitationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
