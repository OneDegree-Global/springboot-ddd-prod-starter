package com.cymetrics.domain.storage.exception;

public class ObjectNotFoundException extends Exception {
    public ObjectNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
