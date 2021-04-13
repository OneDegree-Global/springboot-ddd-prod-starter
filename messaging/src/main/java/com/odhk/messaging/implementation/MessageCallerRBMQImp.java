package com.odhk.messaging.implementation;

import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCaller;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MessageCallerRBMQImp extends MessageProxyRBMQImp implements IMessageCaller {

    private static Logger logger = LoggerFactory.getLogger(MessageCallerRBMQImp.class);

    public MessageCallerRBMQImp() throws QueueLifecycleException {
        super();
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) throws ProtocolIOException{
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
            this.channel.basicPublish("", queueName, props, message);

            Consumer consumer = new DefaultConsumer(this.channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        replyMessage.set(DecodeObject(body));
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


            long limit = System.currentTimeMillis() + timeout;
            while( timeout == 0 || System.currentTimeMillis() < limit){

                if(receiveFlag[0]){
                    this.channel.basicCancel(cTag);
                    return Optional.ofNullable(replyMessage.get());
                }
                this.wait(300);

            }
            this.channel.basicCancel(cTag);
            return Optional.ofNullable(replyMessage.get());
        } catch(IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, String text, int timeout) throws ProtocolIOException {
        try {
            return sendAndGetReply(queueName, EncodeObject(text) , timeout);
        } catch (IOException | ProtocolIOException e) {
            throw new ProtocolIOException(e.getMessage());
        }
    }

    @Override
    public synchronized Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) throws ProtocolIOException {
        byte[] bytes;
        try {
            bytes = EncodeObject(message);
            return sendAndGetReply(queueName, bytes, timeout);
        } catch(IOException | ProtocolIOException e) {
            throw new ProtocolIOException(e.getMessage());
        }

    }

}
