package com.odhk.messaging.Exceptions;

public class QueueLifecycleException extends Exception {
    public QueueLifecycleException(String errorMessage) {
        super(errorMessage);
    }
}
