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

        Consumer consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    callback.onDelivered(DecodeObject(body));
                } catch(IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    // TODO: Log the error
                }
                finally {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                    // log.info("Consumer1 - Ack ok");
                }
            }
            @Override
            public void handleCancel(String consumerTag) throws IOException {
                callback.onCancel();
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, consumer);

        } catch( IOException e){
            // TODO: Log the error
            e.printStackTrace();
            return null;
        }
        return tag;
    }

    @Override
    // Consume then immediate cancel consumer after finished
    public String consumeOnce(String queueName, IMessageCallback callback)  {
        String tag;

        Consumer consumer = new DefaultConsumer(this.channel) {

            boolean isUsed = false;

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    if(isUsed){
                        getChannel().basicReject(envelope.getDeliveryTag(), true);
                        return;
                    }
                    callback.onDelivered(DecodeObject(body));
                    getChannel().basicCancel(consumerTag);
                } catch(IOException | ClassNotFoundException e) {
                    // TODO: Log the error
                    e.printStackTrace();
                }
                finally {
                    if(!isUsed) {
                        getChannel().basicAck(envelope.getDeliveryTag(), false);
                        isUsed = true;
                    }
                }
            }
            @Override
            public void handleCancel(String consumerTag) throws IOException {
                callback.onCancel();
            }
        };

        try {
            tag = this.channel.basicConsume( queueName, false, consumer);

        } catch( IOException e){
            // TODO: Log the error
            e.printStackTrace();
            return "";
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
                return Optional.empty();
            }
            message = DecodeObject((response.getBody()));
        } catch(IOException | ClassNotFoundException e) {
            // TODO: Log the error
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Object> receive(String queueName, int timeout) throws IllegalArgumentException {
        if(timeout<0)
            throw new IllegalArgumentException("timeout should not less than 0");
        AtomicReference<Object> message = new AtomicReference<>(null);

        try {
            long limit = System.currentTimeMillis() + timeout;
            while( timeout == 0  || System.currentTimeMillis() < limit){
                GetResponse response = this.channel.basicGet(queueName, true);
                if(response == null || response.getMessageCount() <= 0){
                    Thread.sleep(1000);
                    continue;
                }
                message.set(DecodeObject(response.getBody()));
                break;
            }
        }catch(IOException | ClassNotFoundException | InterruptedException e) {
            // TODO: Log the error
            Thread.currentThread().interrupt();
        }
        return Optional.ofNullable(message.get());

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