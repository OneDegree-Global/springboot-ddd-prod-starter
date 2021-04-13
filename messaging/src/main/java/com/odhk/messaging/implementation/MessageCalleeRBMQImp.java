package com.odhk.messaging.implementation;

import com.odhk.messaging.exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageCallee;
import com.rabbitmq.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;


public class MessageCalleeRBMQImp extends MessageProxyRBMQImp implements IMessageCallee {

    private static Logger logger = LoggerFactory.getLogger(MessageCalleeRBMQImp.class);

    public MessageCalleeRBMQImp() throws QueueLifecycleException{
        super();
    }

    @Override
    public synchronized Optional<String> consumeAndReply(String queueName, IMessageCallback callback) {
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
                logger.error("Decode byte message body Error:"+e);
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, EncodeObject(response));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
            }
        };

        try {
            return Optional.ofNullable(channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> {
            })));
        } catch(IOException e){
            logger.error("channel consume error:"+e);
        }
        return Optional.empty();
    }


    @Override
    public void removeCallback(String tag)  {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            logger.error("channel remove consumer error:"+e);
        }
    }
}
