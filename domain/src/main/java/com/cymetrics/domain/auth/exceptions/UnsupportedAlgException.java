package com.cymetrics.domain.auth.exceptions;

public class UnsupportedAlgException extends Exception {
    public UnsupportedAlgException(String errorMessage) {
        super(errorMessage);
    }
}
