package com.odhk.messaging.exceptions;

public class QueueLifecycleException extends Exception {
    public QueueLifecycleException(String errorMessage) {
        super(errorMessage);
    }
}
