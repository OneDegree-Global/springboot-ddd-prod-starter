package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessagePublisher;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessagePublisherRBMQImp extends MessageProxyRBMQImp implements IMessagePublisher {
    private static Logger logger = LoggerFactory.getLogger(MessagePublisherRBMQImp.class);
    private Channel channel;

    public MessagePublisherRBMQImp() throws QueueLifecycleException {
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public void publish(String topic, byte[] message) throws ProtocolIOException {
        try {
            this.channel.exchangeDeclare(topic,"fanout");
            this.channel.basicPublish(topic,"", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            logger.error("Producer publish error:"+e);
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public void publish(String topic, String text) throws ProtocolIOException {
        try {
            publish(topic, ObjectByteConverter.encodeObject(text));
        } catch( ProtocolIOException | IOException e ){
            logger.error("Encode text message error:"+e);
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public void publish(String topic, Object message) throws ProtocolIOException {
        try {
            byte[] bytes = ObjectByteConverter.encodeObject(message);
            publish(topic, bytes);
        } catch(ProtocolIOException | IOException e) {
            logger.error("Encode object message error:"+e);
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
