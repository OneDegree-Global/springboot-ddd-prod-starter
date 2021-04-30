package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.messaging.IMessageCaller;
import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


public class MessageCallerRBMQImp implements IMessageCaller {

    private static Logger logger = LoggerFactory.getLogger(MessageCallerRBMQImp.class);
    private Channel channel;
    Clock clock = Clock.systemUTC();

    public MessageCallerRBMQImp() throws QueueLifecycleException {
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) throws ProtocolIOException{
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");
        String cTag = null;

        try {
            AtomicReference<Object> replyMessage = new AtomicReference<>();
            final boolean[] receiveFlag = {false};

            String replyQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            this.channel.queueDeclare(queueName,false,false,false,null);
            this.channel.basicPublish("", queueName, props, message);

            Consumer consumer = new DefaultConsumer(this.channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        replyMessage.set(ObjectByteConverter.decodeObject(body));
                    } catch(IOException | ClassNotFoundException e) {
                        logger.error("Decode byte message error: "+ e);
                    }
                    finally {
                        getChannel().basicAck(envelope.getDeliveryTag(), false);
                        receiveFlag[0] = true;
                    }
                }
            };

            cTag = this.channel.basicConsume(replyQueueName,  consumer);

            long limit = clock.instant().toEpochMilli() + timeout;
            while( timeout == 0 || clock.instant().toEpochMilli() < limit){
                if(receiveFlag[0]) {
                    this.channel.basicCancel(cTag);
                    return Optional.ofNullable(replyMessage.get());
                }

                Thread.sleep(300);

            }

            this.channel.basicCancel(cTag);
            return Optional.ofNullable(replyMessage.get());
        } catch(IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, String text, int timeout) throws ProtocolIOException {
        try {
            return sendAndGetReply(queueName, ObjectByteConverter.encodeObject(text) , timeout);
        } catch (IOException | ProtocolIOException e) {
            throw new ProtocolIOException(e.toString());
        }
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) throws ProtocolIOException {
        byte[] bytes;
        try {
            bytes = ObjectByteConverter.encodeObject(message);
            return sendAndGetReply(queueName, bytes, timeout);
        } catch(IOException | ProtocolIOException e) {
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
