package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.Exceptions.ProtocolIOException;
import com.odhk.messaging.Exceptions.QueueLifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.odhk.messaging.*;
import com.rabbitmq.client.MessageProperties;


public class MessagePublisherRBMQImp extends MessageProxyRBMQImp implements IMessagePublisher{
    private static Logger logger = LoggerFactory.getLogger(MessagePublisherRBMQImp.class);


    public MessagePublisherRBMQImp() throws QueueLifecycleException {
        super();
    }

    @Override
    public synchronized void publish(String topic, byte[] message) throws ProtocolIOException {
        try {
            this.channel.basicPublish(topic,"", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            logger.error("Producer publish error:"+e);
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized void publish(String topic, String text) throws ProtocolIOException {
        try {
            publish(topic, EncodeObject(text));
        } catch( ProtocolIOException | IOException e ){
            logger.error("Encode text message error:"+e);
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized void publish(String topic, Object message) throws ProtocolIOException {
        try {
            byte[] bytes = EncodeObject(message);
            publish(topic, bytes);
        } catch(ProtocolIOException | IOException e) {
            logger.error("Encode object message error:"+e);
            throw new ProtocolIOException(e.getMessage());
        }
    }
}
