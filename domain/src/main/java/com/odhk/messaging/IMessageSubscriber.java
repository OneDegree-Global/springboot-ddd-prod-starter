package com.odhk.messaging;

public interface IMessageSubscriber extends IMessageQueueProxy {
   void subscribe(String topic, String queueName, IMessageCallback callback);
   void unsubscribe(String topic, String queueName);
   void removeCallback(String tag);
}
