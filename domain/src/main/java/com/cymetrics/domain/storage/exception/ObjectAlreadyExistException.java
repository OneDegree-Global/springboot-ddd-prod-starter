package com.cymetrics.domain.storage.exception;

public class ObjectAlreadyExistException extends Exception{
    public ObjectAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
