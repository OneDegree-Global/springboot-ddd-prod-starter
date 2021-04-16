package com.odhk.messaging;

import com.odhk.messaging.exceptions.QueueLifecycleException;

import java.io.Closeable;
import java.util.Optional;

public interface IMessageCallee extends Closeable {

    // used as RPC Function
    Optional<String> consumeAndReply(String queueName, IMessageCallback callback);
    void removeCallback(String tag) ;

}
