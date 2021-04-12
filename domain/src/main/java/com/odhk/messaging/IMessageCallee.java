package com.odhk.messaging;

public interface IMessageCallee extends IMessageQueueProxy{

    // used as RPC Function
    String consumeAndReply(String queueName, IMessageCallback callback);
    void removeCallback(String tag);

}
