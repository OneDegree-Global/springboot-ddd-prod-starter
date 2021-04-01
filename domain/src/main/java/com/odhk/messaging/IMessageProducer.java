package com.odhk.messaging;


public interface IMessageProducer extends IMessageQueueProxy {
    // send message to a specific queue
    void send(String queueName, String key, byte[] message);
    void send(String queueName, String key, String text);
    void send(String queueName, String key, Object message);

}