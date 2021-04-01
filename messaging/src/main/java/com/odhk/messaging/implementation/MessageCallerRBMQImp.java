package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageCaller;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class MessageCallerRBMQImp extends MessageProxyRBMQImp implements IMessageCaller {

    public MessageCallerRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");

        try {
            AtomicReference<Object> replyMessage = null;


            String replyQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            this.channel.basicPublish("", queueName, props, message);

            Thread t = new Thread( () ->{
                try {
                    int counter = 0;
                    while( timeout > 0 && counter < timeout){
                        GetResponse response = this.channel.basicGet(replyQueueName, true);

                        if(response == null){
                            Thread.sleep(1000);
                            continue;
                        }

                        if(response.getProps().getCorrelationId().equals(corrId)){
                            replyMessage.set(DecodeObject(response.getBody()));
                            break;
                        }

                    }
                }catch(IOException | ClassNotFoundException | InterruptedException e) {
                    // TODO: Log the error
                    return;
                }
            }
            );
            return Optional.of(replyMessage.get());
        } catch(IOException e){
            // TODO: Log the error
            return null;
        }
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, String text, int timeout) {
        return sendAndGetReply(queueName, text.getBytes(), timeout);
    }

    @Override
    public Optional<Object> sendAndGetReply(String queueName, Object message, int timeout) {
        try {
            byte[] bytes = EncodeObject(message);
            return sendAndGetReply(queueName, bytes, timeout);
        } catch(IOException e) {
            // TODO: Log the error
            return null;
        }
    }

}
