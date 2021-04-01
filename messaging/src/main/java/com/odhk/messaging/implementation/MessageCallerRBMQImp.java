package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageCaller;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;

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
    public Optional<Object> sendAndGetReply(String queueName, byte[] message, int timeout) {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");

        try {
            AtomicReference<Object> replyMessage = new AtomicReference<>();

            String replyQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            this.channel.basicPublish("", queueName, props, message);


            try {
                long limit = System.currentTimeMillis() + timeout;
                while( timeout == 0 || System.currentTimeMillis() < limit){
                    GetResponse response = this.channel.basicGet(replyQueueName, true);

                    if(response == null || response.getMessageCount() <=0 ){
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
                Thread.currentThread().interrupt();
            }

            return Optional.of(replyMessage.get());
        } catch(IOException e){
            // TODO: Log the error
            return Optional.empty();
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
            return Optional.empty();
        }
    }

}
