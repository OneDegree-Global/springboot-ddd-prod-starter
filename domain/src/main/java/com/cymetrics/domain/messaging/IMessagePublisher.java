package com.cymetrics.domain.messaging;

import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;

import java.io.Closeable;

public interface IMessagePublisher extends Closeable {

    // send message to all queues subscribed to a topic
    void publish(String topic, byte[] message) throws ProtocolIOException;
    void publish(String topic, String text) throws ProtocolIOException;
    void publish(String topic, Object message) throws ProtocolIOException;

}
