package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class MessageSubscriberRBMQImp extends MessageConsumerRBMQImp implements IMessageSubscriber {


    public MessageSubscriberRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public String subscribe(String topic, String queueName, IMessageCallback callback) {
        try {
            this.channel.queueBind(queueName, topic, "");
            return super.consume(queueName, callback);
        } catch( IOException e){
            e.printStackTrace();
            // TODO: log the error
        }
        return null;
    }

    @Override
    public void unsubscribe(String topic, String queueName) {
        try {
            this.channel.queueUnbind(queueName, topic, "");
        } catch( IOException e){
            e.printStackTrace();
            // TODO: log the error
        }
    }

}