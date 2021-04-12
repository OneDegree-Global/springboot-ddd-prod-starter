package com.odhk.messaging;


public interface IMessageProducer extends IMessageQueueProxy {
    // send message to a specific queue
    void send(String queueName, byte[] message);
    void send(String queueName, String text);
    void send(String queueName, Object message);

}