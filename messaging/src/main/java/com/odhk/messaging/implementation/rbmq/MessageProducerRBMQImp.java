package com.odhk.messaging.implementation.rbmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.cymetrics.messaging.IMessageProducer;
import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.exceptions.QueueLifecycleException;
import com.odhk.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.*;

public class MessageProducerRBMQImp implements IMessageProducer {

    private Channel channel;

    public MessageProducerRBMQImp() throws ProtocolIOException, QueueLifecycleException {
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public void send(String queueName, byte[] message) throws ProtocolIOException {
        try {
            // Use default exchange if using direct send
            this.channel.queueDeclare(queueName,false,false,false,null);
            this.channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public void send(String queueName, String message) throws ProtocolIOException {
        try {
            send(queueName, ObjectByteConverter.encodeObject(message));
        } catch(ProtocolIOException | IOException e) {
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public void send(String queueName, Object message) throws ProtocolIOException {
        byte[] bytes;
        try {
            bytes = ObjectByteConverter.encodeObject(message);
            send(queueName, bytes);
        } catch(ProtocolIOException | IOException e) {
            throw new ProtocolIOException(e.toString());
        }
    }
    @Override
    public void close() throws IOException{
        try {
            channel.close();
        } catch(IOException | TimeoutException e){
            throw new IOException(e.toString());
        }
    }

}