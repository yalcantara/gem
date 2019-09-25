package com.gem.commons;

public class CommonsException extends RuntimeException {

    public CommonsException() {
    }

    public CommonsException(String message) {
        super(message);
    }

    public CommonsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonsException(Throwable cause) {
        super(cause);
    }

    public CommonsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
