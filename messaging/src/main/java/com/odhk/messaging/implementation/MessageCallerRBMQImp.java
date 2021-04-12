package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageCaller;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class MessageCallerRBMQImp extends MessageProxyRBMQImp implements IMessageCaller {

    public MessageCallerRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");
        String cTag = null;

        try {
            AtomicReference<Object> replyMessage = new AtomicReference<>();
            boolean receiveFlag = false;

            String replyQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            this.channel.basicPublish("", queueName, props, message);
            System.out.println("corrId :"+corrId);
            System.out.println("replyqueue :"+props.getReplyTo());

            Consumer consumer = new DefaultConsumer(this.channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        replyMessage.set(DecodeObject(body));
                    } catch(IOException | ClassNotFoundException e) {
                        // TODO: Log the error
                        e.printStackTrace();
                    }
                    finally {
                        getChannel().basicAck(envelope.getDeliveryTag(), false);
                    }
                }
                @Override
                public void handleCancel(String consumerTag) throws IOException {
                }
            };


            cTag = this.channel.basicConsume(replyQueueName,  consumer);


            long limit = System.currentTimeMillis() + timeout;
            while( timeout == 0 || System.currentTimeMillis() < limit){

                if(receiveFlag){
                    this.channel.basicCancel(cTag);
                    return Optional.ofNullable(replyMessage.get());
                }
                Thread.sleep(300);

            }
            this.channel.basicCancel(cTag);
            return Optional.ofNullable(replyMessage.get());
        } catch(IOException | InterruptedException e){
            // TODO: Log the error
            e.printStackTrace();
            Thread.interrupted();
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, String text, int timeout) {
        try {
            return sendAndGetReply(queueName, EncodeObject(text) , timeout);
        } catch (IOException e) {
            // TODO: Log the error
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) {
        try {
            byte[] bytes = EncodeObject(message);
            return sendAndGetReply(queueName, bytes, timeout);
        } catch(IOException e) {
            // TODO: Log the error
            return Optional.empty();
        }
    }

}
