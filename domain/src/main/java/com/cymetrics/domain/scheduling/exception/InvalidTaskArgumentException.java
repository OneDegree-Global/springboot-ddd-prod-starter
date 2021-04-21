package com.cymetrics.domain.scheduling.exception;

public class InvalidTaskArgumentException extends Exception{
    public InvalidTaskArgumentException(String errorMessage) {
        super(errorMessage);
    }
}
