package com.odhk.messaging;

import com.odhk.messaging.Exceptions.ProtocolIOException;

public interface IMessagePublisher extends IMessageQueueProxy {

    // send message to all queues subscribed to a topic
    void publish(String topic, byte[] message) throws ProtocolIOException;
    void publish(String topic, String text) throws ProtocolIOException;
    void publish(String topic, Object message) throws ProtocolIOException;

}
