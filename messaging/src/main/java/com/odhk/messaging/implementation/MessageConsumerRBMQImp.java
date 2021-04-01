package com.odhk.messaging.implementation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import com.odhk.messaging.*;
import com.rabbitmq.client.*;


public class MessageConsumerRBMQImp extends MessageProxyRBMQImp implements IMessageConsumer, IMessageReceiver {

    public MessageConsumerRBMQImp() throws IOException, TimeoutException {
        super();
    }

    @Override
    public String consume(String queueName,IMessageCallback callback)  {
        String tag;
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                GetResponse response = this.channel.basicGet( queueName, false);
                Object message = DecodeObject(response.getBody());
                callback.onDelivered(message);
            } catch(IOException | ClassNotFoundException e) {
                // TODO: Log the error
                return;
            }
            finally {
                this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, deliverCallback, consumerTag -> {
                callback.onCancel();
            });

        } catch( IOException e){
            // TODO: Log the error
            return null;
        }
        return tag;
    }

    @Override
    // Consume then immediate cancel consumer after finished
    public String consumeOnce(String queueName, IMessageCallback callback)  {
        String tag;
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                GetResponse response = this.channel.basicGet( queueName, false);
                Object message = DecodeObject(response.getBody());
                callback.onDelivered(message);
            } catch(IOException | ClassNotFoundException e) {
                // TODO: Log the error
                return;
            }
            finally {
                this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                this.channel.basicCancel(consumerTag);
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, deliverCallback, consumerTag -> {
                callback.onCancel();
            });

        } catch( IOException e){
            // TODO: Log the error
            return null;
        }
        return tag;
    }

    @Override
    public Optional<Object> tryReceive(String queueName){
        GetResponse response;
        Object message;
        try {
            response = this.channel.basicGet( queueName, true);
            if(response == null){
                return null;
            }
            message = DecodeObject((response.getBody()));
        } catch(IOException | ClassNotFoundException e) {
            // TODO: Log the error
            return null;
        }
        return Optional.of(message);
    }

    @Override
    public Optional<Object> receive(String queueName, int timeout) throws IllegalArgumentException {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");
        AtomicReference<Object> message = null;

        Thread t = new Thread( () ->{
            try {
                int counter = 0;
                while( timeout > 0 && counter < timeout){
                    GetResponse response = this.channel.basicGet(queueName, true);
                    if(response == null){
                        Thread.sleep(1000);
                        continue;
                    }
                    message.set(DecodeObject(response.getBody()));
                }
            }catch(IOException | ClassNotFoundException | InterruptedException e) {
                // TODO: Log the error
                return;
            }
        }
        );
        return Optional.of(message.get());
    }

    @Override
    public void removeCallback(String tag) {
        try {
            this.channel.basicCancel(tag);
        } catch( IOException e){
            // TODO: Log the error
        }
    }


    // ONLY for test purpose
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);

        Connection connection;
        try {
            connection = factory.newConnection();
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();



        boolean durable = true;
        channel1.queueDeclare("testQueue", durable, false, false, null);

        Consumer consumer1 = new DefaultConsumer(channel1) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    System.out.println(consumerTag);
                    System.out.println(envelope.getDeliveryTag());
                    doWork("Consumer1", body);
                } finally {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                    // log.info("Consumer1 - Ack ok");
                }
            }
        };

        Consumer consumer2 = new DefaultConsumer(channel2) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    System.out.println(consumerTag);
                    System.out.println(envelope.getDeliveryTag());
                    doWork("Consumer2", body);
                } finally {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                    //log.info("Consumer2 - Ack ok");
                    getChannel().basicAck(envelope.getDeliveryTag(), false); // ack again is ok
                    //log.info("Consumer2 - Ack again ok");
                }
            }
        };

        boolean autoAck = false;

        GetResponse gr1 = channel1.basicGet("testQueue", true);
        System.out.println(gr1.toString());
        System.out.println(new String(gr1.getBody(), StandardCharsets.UTF_8) );
        GetResponse gr2 = channel2.basicGet("testQueue", true);
        System.out.println(new String(gr2.getBody(), StandardCharsets.UTF_8) );

        //channel1.basicConsume("testQueue", autoAck, consumer1);
        //channel2.basicConsume("testQueue", autoAck, consumer2);
    }

    private static void doWork(String name, byte[] body) {
        String message = new String(body);
        System.out.println(name);
        //log.info(name + " - Message received: " + message);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //log.info(name + " - Work done! " + message);
    }
}