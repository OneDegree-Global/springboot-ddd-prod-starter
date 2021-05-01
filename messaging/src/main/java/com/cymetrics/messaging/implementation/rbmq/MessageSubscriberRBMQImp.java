package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessageSubscriber;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


public class MessageSubscriberRBMQImp implements IMessageSubscriber {

    private static Logger logger = LoggerFactory.getLogger(MessageSubscriberRBMQImp.class);
    private Channel channel;
    private MessageConsumerRBMQImp consumer;

    public MessageSubscriberRBMQImp() throws ProtocolIOException, QueueLifecycleException {
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
            this.consumer = new MessageConsumerRBMQImp();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public Optional<String> subscribe(String topic, String queueName, IMessageCallback callback) {
        try {
            this.channel.exchangeDeclare(topic,"fanout");
            this.channel.queueDeclare(queueName,false, false, false, null);
            this.channel.queueBind(queueName, topic, "");
            return consumer.consume(queueName, callback);
        } catch( IOException e){
            logger.error("subscriber subscribe message error:"+e);
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

    @Override
    public void removeCallback(String tag) {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            logger.error("channel remove consumer error:"+e);
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