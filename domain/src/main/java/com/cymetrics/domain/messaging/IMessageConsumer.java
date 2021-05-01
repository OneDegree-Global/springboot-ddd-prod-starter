package com.cymetrics.domain.messaging;

import java.io.Closeable;
import java.util.Optional;

public interface IMessageConsumer extends Closeable {

    // Non-blocking callback, should provide identifier for callback
    Optional<String> consume(String queueName, IMessageCallback callback);
    Optional<String> consumeOnce(String queueName, IMessageCallback callback);

    void removeCallback(String tag) ;

}