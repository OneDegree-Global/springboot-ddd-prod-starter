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
    public synchronized String consumeAndReply(String queueName, IMessageCallback callback) {
        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            Object response = new Object();
            System.out.println("callee corid: "+delivery.getProperties().getCorrelationId());

            try {
                Object message = DecodeObject(delivery.getBody());
                response = callback.onCalled(message);
            } catch (RuntimeException | ClassNotFoundException e ) {
                // TODO: Log the error
                e.printStackTrace();
            } finally {
                System.out.println("reply to :"+delivery.getProperties().getReplyTo());
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, EncodeObject(response));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
            }
        };

        try {
            return channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {
            }));
        } catch(IOException e){
            e.printStackTrace();
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
