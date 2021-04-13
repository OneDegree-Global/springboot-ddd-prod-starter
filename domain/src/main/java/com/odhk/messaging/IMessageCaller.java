package com.odhk.messaging;

import com.odhk.messaging.Exceptions.ProtocolIOException;

import java.util.Optional;

public interface IMessageCaller extends IMessageQueueProxy {

    // Used as RPC
    Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) throws ProtocolIOException;
    Optional<Object> sendAndGetReply(String queueName, String text, int timeout) throws ProtocolIOException;
    Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) throws ProtocolIOException;

}
