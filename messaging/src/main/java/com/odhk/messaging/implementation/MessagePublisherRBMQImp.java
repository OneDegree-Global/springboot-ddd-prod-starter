package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.*;
import com.rabbitmq.client.MessageProperties;


public class MessagePublisherRBMQImp extends MessageProxyRBMQImp implements IMessagePublisher{


    public MessagePublisherRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public synchronized void publish(String topic, byte[] message) {
        try {
            this.channel.basicPublish(topic,"", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            // TODO: log the error
        }
    }

    @Override
    public synchronized void publish(String topic, String text)  {
        try {
            publish(topic, EncodeObject(text));
        } catch( IOException e ){
            // TODO: Log the error
        }
    }

    @Override
    public synchronized void publish(String topic, Object message) {
        try {
            byte[] bytes = EncodeObject(message);
            publish(topic, bytes);
        } catch(IOException e) {
            // TODO: Log the error
        }
    }
}
