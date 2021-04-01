package com.odhk.messaging;

public interface IMessageCaller extends IMessageQueueProxy {
    // Used as RPC
    Object sendAndGetReply(String queueName, byte[] message, int timeout);
    Object sendAndGetReply(String queueName, String text, int timeout);
    Object sendAndGetReply(String queueName, Object message, int timeout);

}
