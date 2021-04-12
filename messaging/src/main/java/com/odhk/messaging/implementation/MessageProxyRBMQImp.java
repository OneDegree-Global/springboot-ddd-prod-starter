package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageQueueProxy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;


public class MessageProxyRBMQImp implements IMessageQueueProxy, AutoCloseable {

    private  Connection connection;
    protected Channel channel;
    protected CopyOnWriteArrayList<String> queueNames = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<String> exchangeNames = new CopyOnWriteArrayList<>();

    public MessageProxyRBMQImp() throws IOException, TimeoutException {
        ChannelPoolRBMQImp connectionPool = ChannelPoolRBMQImp.getInstance();
        this.connection = connectionPool.getConnection();
        this.channel = this.connection.createChannel();
    }

    @Override
    public boolean createQueue(String name){
        try {
            this.channel.queueDeclare(name, false, false, false, null);
        } catch(IOException  e){
            // TODO: Log the error
            return false;
        }
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
        } catch(IOException  e){
            // TODO: log the error
        }
    }

    @Override
    public boolean createTopic(String name) {
        try {
           this.channel.exchangeDeclare(name, "fanout");
        } catch(IOException  e){
            // TODO: Log the error
            return false;
        }
        this.exchangeNames.add(name);
        return true;
    }

    @Override
    public void deleteTopic(String name) {
        try{
            this.channel.exchangeDelete(name);
        } catch(IOException e){
            // TODO: log the error
        }
    }

    protected byte[] EncodeObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }


    protected Object DecodeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // TODO: until all message objects have been defined, we cannot create white list
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }
}
