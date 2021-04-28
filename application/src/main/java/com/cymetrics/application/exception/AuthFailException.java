package com.cymetrics.application.exception;

public class AuthFailException extends Exception {
    public AuthFailException(String errorMessage) {
        super(errorMessage);
    }
}
