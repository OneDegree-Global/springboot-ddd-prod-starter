package com.odhk.messaging;

import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.exceptions.QueueLifecycleException;

import java.io.Closeable;

public interface IMessageProducer extends Closeable {
    // send message to a specific queue
    void send(String queueName, byte[] message) throws ProtocolIOException;
    void send(String queueName, String text) throws ProtocolIOException;
    void send(String queueName, Object message) throws ProtocolIOException;

}