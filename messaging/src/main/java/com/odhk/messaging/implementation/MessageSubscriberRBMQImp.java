package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.*;


public class MessageSubscriberRBMQImp extends MessageProxyRBMQImp implements IMessageSubscriber {


    public MessageSubscriberRBMQImp() throws IOException, TimeoutException {
        super();
    }


    @Override
    public void subscribe(String topic, String queueName, IMessageCallback callback) {
        try {
            this.channel.queueBind(queueName, topic, "");
        } catch( IOException e){
            // TODO: log the error
        }
    }

    @Override
    public void unsubscribe(String topic, String queueName) {
        try {
            this.channel.queueUnbind(queueName, topic, "");
        } catch( IOException e){
            // TODO: log the error
        }
    }

    @Override
    public void removeCallback(String tag) {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            // TODO: Log the error
        }
    }
}