package com.odhk.messaging;

import com.odhk.messaging.exceptions.ProtocolIOException;

public interface IMessageProducer  {
    // send message to a specific queue
    void send(String queueName, byte[] message) throws ProtocolIOException;
    void send(String queueName, String text) throws ProtocolIOException;
    void send(String queueName, Object message) throws ProtocolIOException;

}