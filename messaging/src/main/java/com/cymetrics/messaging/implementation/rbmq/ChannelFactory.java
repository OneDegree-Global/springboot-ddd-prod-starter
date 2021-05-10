package com.cymetrics.messaging.implementation.rbmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChannelFactory {

    @Inject
    static RBMQConfig config;

    private static  ChannelFactory instance;
    private Connection connection;
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
            synchronized (connection) {
                c = connection.createChannel();
            }
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
        factory.setUsername(config.getUserName());
        factory.setPassword(config.getPassword());
        factory.setHost(config.getHost());

        factory.setPort(config.getPort());
        this.connection = factory.newConnection();
        this.threadLocalChannel = new ThreadLocal<>();

    }
}
