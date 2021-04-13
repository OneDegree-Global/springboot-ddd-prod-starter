package com.odhk.messaging;

import java.util.Optional;

public interface IMessageCallee extends IMessageQueueProxy{

    // used as RPC Function
    Optional<String> consumeAndReply(String queueName, IMessageCallback callback);
    void removeCallback(String tag) ;

}
