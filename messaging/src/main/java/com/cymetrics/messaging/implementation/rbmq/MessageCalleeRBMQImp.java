package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessageCallee;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


public class MessageCalleeRBMQImp extends MessageProxyRBMQImp implements IMessageCallee {

    private static Logger logger = LoggerFactory.getLogger(MessageCalleeRBMQImp.class);
    private Channel channel;

    public MessageCalleeRBMQImp() throws QueueLifecycleException{
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public Optional<String> consumeAndReply(String queueName, IMessageCallback callback) {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            Object response = new Object();

            try {
                Object message = ObjectByteConverter.decodeObject(delivery.getBody());
                response = callback.onCalled(message);
            } catch (RuntimeException | ClassNotFoundException e ) {
                logger.error("Decode byte message body Error:"+e);
            } finally {
                this.channel.queueDeclare(queueName,false,false,false,null);

                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, ObjectByteConverter.encodeObject(response));
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

    @Override
    public void close() throws IOException{
        try {
            channel.close();
        } catch(IOException | TimeoutException e){
            throw new IOException(e.toString());
        }
    }
}
