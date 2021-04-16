package com.odhk.messaging.implementation.rbmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChannelFactory {

    // TODO: Use DI to inject MQ Config
    static String userName = "admin";
    static String password = "admin";
    static String host = "127.0.0.1";
    static int port = 5672;

    private static volatile ChannelFactory instance;
    private volatile Connection connection;
    private ThreadLocal<Channel> threadLocalChannel;

    public synchronized static ChannelFactory createInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = new ChannelFactory();
        }
        return instance;
    }

    public static ChannelFactory getInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = createInstance();
        }
        return instance;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public Channel getChannel() throws IOException {
        Channel c = threadLocalChannel.get();
        if (c == null) {
            c = connection.createChannel();
            threadLocalChannel.set(c);
            c = threadLocalChannel.get();
        }
        return c;
    }

    private ChannelFactory() throws IOException, TimeoutException {
        init();
    }

    private void init() throws IOException, TimeoutException{
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        this.connection = factory.newConnection();
        this.threadLocalChannel = new ThreadLocal<>();

    }
}
