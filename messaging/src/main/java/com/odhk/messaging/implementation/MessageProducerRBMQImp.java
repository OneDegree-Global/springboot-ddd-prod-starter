package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

import com.odhk.messaging.*;

public class MessageProducerRBMQImp extends MessageProxyRBMQImp implements IMessageProducer {

    public MessageProducerRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public void send(String queueName, String key, byte[] message) {
        try {
            // Use default exchange if using direct send
            this.channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch(IOException e){
            // TODO: Log the error
            return;
        }
    }

    @Override
    public void send(String queueName, String key, String message) {
        send(queueName, key, message.getBytes());
    }

    @Override
    public void send(String queueName, String key, Object message) {
        try {
            byte[] bytes = EncodeObject(message);
            send(queueName, key, bytes);
        } catch(IOException e) {
            // TODO: Log the error
            return;
        }
    }



    // ONLY For test purpose
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        System.out.println("start producing");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        boolean durable = true;
        channel.queueDeclare("testQueue", durable, false, false, null);

        for (int i = 0; i < 10; i++) {
            String message = "Hello World! " + i;
            channel.basicPublish("", "testQueue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            // log.info("Message sent: " + message);
            System.out.println("Message sent: " + message);
            // Thread.sleep(500);
        }

        channel.close();
        connection.close();
    }
}