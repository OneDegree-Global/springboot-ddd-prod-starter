package com.cymetrics.domain.messaging.exceptions;

public class QueueLifecycleException extends Exception {
    public QueueLifecycleException(String errorMessage) {
        super(errorMessage);
    }
}
