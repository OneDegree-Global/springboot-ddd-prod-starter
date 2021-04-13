package com.odhk.messaging.implementation;

import java.io.IOException;

import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.exceptions.QueueLifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rabbitmq.client.*;

import com.odhk.messaging.*;

public class MessageProducerRBMQImp extends MessageProxyRBMQImp implements IMessageProducer {

    private static Logger logger = LoggerFactory.getLogger(MessageProducerRBMQImp.class);

    public MessageProducerRBMQImp() throws ProtocolIOException, QueueLifecycleException {
        super();
    }

    @Override
    public synchronized void send(String queueName, byte[] message) throws ProtocolIOException {
        try {
            // Use default exchange if using direct send
            this.channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized void send(String queueName, String message) throws ProtocolIOException {
        try {
            send(queueName, EncodeObject(message));
        } catch(ProtocolIOException | IOException e) {
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized void send(String queueName, Object message) throws ProtocolIOException {
        byte[] bytes;
        try {
            bytes = EncodeObject(message);
            send(queueName, bytes);
        } catch(ProtocolIOException | IOException e) {
            throw new ProtocolIOException(e.getMessage());
        }
    }


}