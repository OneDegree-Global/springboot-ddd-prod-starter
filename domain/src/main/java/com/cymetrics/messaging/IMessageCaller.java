package com.cymetrics.messaging;

import com.cymetrics.messaging.exceptions.ProtocolIOException;

import java.io.Closeable;
import java.util.Optional;

public interface IMessageCaller extends Closeable {

    // Used as RPC
    Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) throws ProtocolIOException;
    Optional<Object> sendAndGetReply(String queueName, String text, int timeout) throws ProtocolIOException;
    Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) throws ProtocolIOException;

}
