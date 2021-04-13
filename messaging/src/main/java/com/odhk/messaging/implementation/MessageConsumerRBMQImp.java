package com.odhk.messaging.implementation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import com.odhk.messaging.Exceptions.ProtocolIOException;
import com.odhk.messaging.Exceptions.QueueLifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.odhk.messaging.*;
import com.rabbitmq.client.*;


public class MessageConsumerRBMQImp extends MessageProxyRBMQImp implements IMessageConsumer, IMessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(MessageConsumerRBMQImp.class);

    public MessageConsumerRBMQImp() throws ProtocolIOException, QueueLifecycleException {
        super();
    }

    @Override
    public Optional<String> consume(String queueName,IMessageCallback callback)  {
        String tag;

        Consumer consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    callback.onDelivered(DecodeObject(body));
                } catch(IOException | ClassNotFoundException e) {
                    logger.error("Decode byte message body error:"+e);
                }
                finally {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                    // log.info("Consumer1 - Ack ok");
                }
            }
            @Override
            public void handleCancel(String consumerTag) throws IOException {
                callback.onCancel();
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, consumer);
        } catch( IOException e){
            logger.error("channel consume error:"+e);
            return Optional.empty();
        }
        return Optional.ofNullable(tag);
    }

    @Override
    // Consume then immediate cancel consumer after finished
    public Optional<String> consumeOnce(String queueName, IMessageCallback callback)  {
        String tag;

        Consumer consumer = new DefaultConsumer(this.channel) {

            boolean isUsed = false;

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    if(isUsed){
                        getChannel().basicReject(envelope.getDeliveryTag(), true);
                        return;
                    }
                    callback.onDelivered(DecodeObject(body));
                    getChannel().basicCancel(consumerTag);
                } catch(IOException | ClassNotFoundException e) {
                    logger.error("Decode byte message body error:"+e);
                }
                finally {
                    if(!isUsed) {
                        getChannel().basicAck(envelope.getDeliveryTag(), false);
                        isUsed = true;
                    }
                }
            }
            @Override
            public void handleCancel(String consumerTag) throws IOException {
                callback.onCancel();
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, consumer);

        } catch( IOException e){
            logger.error("channel consume error:"+e);
            return Optional.empty();
        }
        return Optional.ofNullable(tag);
    }

    @Override
    public Optional<Object> tryReceive(String queueName){
        GetResponse response;
        Object message;
        try {
            response = this.channel.basicGet( queueName, true);
            if(response == null){
                return Optional.empty();
            }
            message = DecodeObject((response.getBody()));
        } catch(IOException | ClassNotFoundException e) {
            logger.error("Decode byte message body error:"+e);
            return Optional.empty();
        }
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Object> receive(String queueName, int timeout) throws IllegalArgumentException {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");
        AtomicReference<Object> message = new AtomicReference<>(null);

        try {
            long limit = System.currentTimeMillis() + timeout;
            while( timeout == 0  || System.currentTimeMillis() < limit){
                GetResponse response = this.channel.basicGet(queueName, true);
                if(response == null || response.getMessageCount() <= 0){
                    Thread.sleep(1000);
                    continue;
                }
                message.set(DecodeObject(response.getBody()));
                break;
            }
        }catch(IOException | ClassNotFoundException | InterruptedException e) {
            logger.error("Consumer receive message error:"+e);
            Thread.currentThread().interrupt();
        }
        return Optional.ofNullable(message.get());

    }

    @Override
    public void removeCallback(String tag) {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            logger.error("channel remove consumer error:"+e);
        }
    }


}