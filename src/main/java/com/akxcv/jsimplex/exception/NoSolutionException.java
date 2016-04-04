package com.akxcv.jsimplex.exception;

/**
 * Created by ak on 02.04.16.
 */
public class NoSolutionException extends Exception {

    public NoSolutionException() {
        this("Функция не ограничена");
    }

    public NoSolutionException(String message) {
        super(message);
    }

    public NoSolutionException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
