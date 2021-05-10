package com.cymetrics.domain.messaging;

import java.io.Closeable;
import java.util.Optional;

public interface IMessageReceiver extends Closeable {

    // If queue is empty, should return null
    Optional<Object> tryReceive(String queueName);
    // Blocking until the message is received, timeout = 0 means no timeout
    Optional<Object> receive(String queueName, int timeout);

}
