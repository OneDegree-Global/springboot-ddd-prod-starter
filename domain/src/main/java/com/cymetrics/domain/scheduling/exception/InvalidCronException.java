package com.cymetrics.domain.scheduling.exception;

public class InvalidCronException extends Exception {
    public InvalidCronException(String errorMessage) {
        super(errorMessage);
    }
}

