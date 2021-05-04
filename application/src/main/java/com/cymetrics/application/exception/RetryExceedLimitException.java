package com.cymetrics.application.exception;

public class RetryExceedLimitException extends Exception {
    public RetryExceedLimitException(String errorMessage) {
        super(errorMessage);
    }
}