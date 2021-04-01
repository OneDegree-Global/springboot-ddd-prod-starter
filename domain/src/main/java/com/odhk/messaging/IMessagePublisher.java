package com.odhk.messaging;

public interface IMessagePublisher extends IMessageQueueProxy {

    // send message to all queues subscribed to a topic
    void publish(String topic, byte[] message);
    void publish(String topic, String text);
    void publish(String topic, Object message);

}
