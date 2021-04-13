package com.odhk.messaging.implementation;

import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;


public class MessageSubscriberRBMQImp extends MessageConsumerRBMQImp implements IMessageSubscriber {

    private static Logger logger = LoggerFactory.getLogger(MessageSubscriberRBMQImp.class);

    public MessageSubscriberRBMQImp() throws ProtocolIOException, QueueLifecycleException {
        super();
    }

    @Override
    public Optional<String> subscribe(String topic, String queueName, IMessageCallback callback) {
        try {
            this.channel.queueBind(queueName, topic, "");
            return super.consume(queueName, callback);
        } catch( IOException e){
            logger.error("subscriber subscirbe message error:"+e);
        }
        return Optional.empty();
    }

    @Override
    public void unsubscribe(String topic, String queueName) {
        try {
            this.channel.queueUnbind(queueName, topic, "");
        } catch( IOException e){
            logger.error("subscriber unsubscribe error:"+e);
        }
    }

}