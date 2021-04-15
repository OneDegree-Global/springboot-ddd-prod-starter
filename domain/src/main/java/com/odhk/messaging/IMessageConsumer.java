package com.odhk.messaging;

import java.util.Optional;

public interface IMessageConsumer  {

    // Non-blocking callback, should provide identifier for callback
    Optional<String> consume(String queueName, IMessageCallback callback);
    Optional<String> consumeOnce(String queueName, IMessageCallback callback);

    void removeCallback(String tag) ;
}