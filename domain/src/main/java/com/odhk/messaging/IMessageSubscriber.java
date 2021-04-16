package com.odhk.messaging;

import com.odhk.messaging.exceptions.QueueLifecycleException;

import java.io.Closeable;
import java.util.Optional;

public interface IMessageSubscriber extends Closeable {

   // Bind a queue to a specific exchange, so that messages sent to that exchange
   // will be forwarded to the queue
   Optional<String> subscribe(String topic, String queueName, IMessageCallback callback);
   void unsubscribe(String topic, String queueName);
   void removeCallback(String tag);

}
