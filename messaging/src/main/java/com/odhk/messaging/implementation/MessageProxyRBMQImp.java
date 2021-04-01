package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageQueueProxy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class MessageProxyRBMQImp implements IMessageQueueProxy, AutoCloseable {

    // TODO: Use DI to inject MQ Config
    protected static String userName = "admin";
    protected static String password = "admin";
    protected static String host = "127.0.0.1";
    protected static int port = 5672;

    protected ConnectionFactory factory;
    protected Connection connection;
    protected Channel channel;
    protected List<String> queueNames = new ArrayList<>();
    protected List<String> exchangeNames = new ArrayList<>();

    public MessageProxyRBMQImp() throws IOException, TimeoutException {
        this.factory = new ConnectionFactory();
        this.factory.setUsername(userName);
        this.factory.setPassword(password);
        this.factory.setHost(host);
        this.factory.setPort(port);
        createSession();
    }

    @Override
    public boolean createQueue(String name){
        try {
            this.channel.queueDeclare(name, false, false, false, null);
        } catch(IOException  e){
            // TODO: Log the error
            return false;
        }
        //QueueConfig config = new QueueConfig(name);
        this.queueNames.add(name);
        return true;
    }

//    @Override
//    // if parameter is not supported in this MQ, should set in queueConfig
//    public Optional<QueueConfig> createQueue(String name, boolean durable, boolean autoACK, int prefetchCount) {
//        try {
//            this.channel.basicQos(prefetchCount);
//            this.channel.queueDeclare(name, durable, false, false, null);
//        } catch(IOException e) {
//            // TODO: Log the error
//            return;
//        }
//        QueueConfig config = new QueueConfig(name, durable,autoACK, prefetchCount);
//        this.configs.add(config);
//        return Optional.of(config);
//    }
    @Override
    public void close(){

    }

    @Override
    public void deleteQueue(String name) {
        try{
            this.channel.queueDelete(name);
        } catch(IOException e){
            // TODO: log the error
        }
    }

    @Override
    public boolean createExchange(String name, String type) {
        try {
           this.channel.exchangeDeclare(name, type);
        } catch(IOException  e){
            // TODO: Log the error
            return false;
        }
        this.exchangeNames.add(name);
        return true;
    }

    @Override
    public void deleteExchange(String name) {
        try{
            this.channel.exchangeDelete(name);
        } catch(IOException e){
            // TODO: log the error
        }
    }


    private void createSession() throws IOException, TimeoutException {
        if(this.connection == null || !this.connection.isOpen())
            this.connection = factory.newConnection();
        if(this.channel == null || !this.channel.isOpen())
            this.channel = connection.createChannel();
    }

    protected byte[] EncodeObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }

    protected Object DecodeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }
}
