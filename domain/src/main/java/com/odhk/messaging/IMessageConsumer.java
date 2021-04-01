package com.odhk.messaging;

public interface IMessageConsumer extends IMessageQueueProxy {
    // Non-blocking callback, should provide identifier for callback
    String consume(String queueName, IMessageCallback callback);
    String consumeOnce(String queueName, IMessageCallback callback);

    void removeCallback(String tag);
}