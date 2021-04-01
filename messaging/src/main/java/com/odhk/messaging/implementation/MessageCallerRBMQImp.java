package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageCaller;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class MessageCallerRBMQImp extends MessageProxyRBMQImp implements IMessageCaller {

    public MessageCallerRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public Object sendAndGetReply(String queueName, byte[] message, int timeout) {
        try {
            String replyQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            this.channel.basicPublish("", queueName, props, message);

            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result = response.take();
            channel.basicCancel(ctag);
            return result;

        } catch(IOException | InterruptedException e){
            // TODO: Log the error
            return null;
        }
    }

    @Override
    public Object sendAndGetReply(String queueName, String text, int timeout) {
        return null;
    }

    @Override
    public Object sendAndGetReply(String queueName, Object message, int timeout) {
        try {
            byte[] bytes = EncodeObject(message);
            return sendAndGetReply(queueName, bytes, timeout);
        } catch(IOException e) {
            // TODO: Log the error
            return null;
        }
    }

}
