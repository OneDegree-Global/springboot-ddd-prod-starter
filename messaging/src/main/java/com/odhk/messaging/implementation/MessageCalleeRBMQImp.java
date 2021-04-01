package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageCallee;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MessageCalleeRBMQImp extends MessageProxyRBMQImp implements IMessageCallee {
    public MessageCalleeRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public String consumeAndReply(String queueName, IMessageCallback callback) {
        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            Object response = new Object();

            try {
                Object message = DecodeObject(delivery.getBody());
                response = callback.onCalled(message);
            } catch (RuntimeException | ClassNotFoundException e ) {
                // TODO: Log the error
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, EncodeObject(response));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        try {
            return channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {
            }));
        } catch(IOException e){
            // TODO: Log the error
        }
        return "";
    }


    @Override
    public void removeCallback(String tag) {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            // TODO: Log the error
        }
    }
}
