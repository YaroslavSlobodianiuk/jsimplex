package com.akxcv.jsimplex.exception;

/**
 * Created by ak on 02.04.16.
 */
public class FunctionNotLimitedException extends Exception {

    public FunctionNotLimitedException() {
        this("Функция не ограничена");
    }

    public FunctionNotLimitedException(String message) {
        super(message);
    }

    public FunctionNotLimitedException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
