package com.odhk.messaging;

import java.util.Optional;

public interface IMessageCaller extends IMessageQueueProxy {

    // Used as RPC
    Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout);
    Optional<Object> sendAndGetReply(String queueName, String text, int timeout);
    Optional<Object> sendAndGetReply(String queueName, Object message, int timeout);

}
