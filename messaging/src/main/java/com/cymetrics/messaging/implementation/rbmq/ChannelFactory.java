package com.cymetrics.messaging.implementation.rbmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChannelFactory {

    static RBMQConfig config;

    private static ChannelFactory instance;
    private Connection connection;
    private ThreadLocal<Channel> threadLocalChannel;

    public ChannelFactory() throws IOException, TimeoutException {
        if (config == null)
            config = new RBMQConfig("guest",
                    "guest",
                    "127.0.0.1",
                    5672);
    }

    public synchronized static ChannelFactory createInstance() throws IOException, TimeoutException {
        if (instance == null) {
            instance = new ChannelFactory();
        }
        return instance;
    }

    public static ChannelFactory getInstance() throws IOException, TimeoutException {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    public static void setConfig(RBMQConfig rbmqConfig) {
        config = rbmqConfig;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Channel getChannel() throws IOException, TimeoutException {
        init();
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

    private void init() throws IOException, TimeoutException {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setUsername(config.getUserName());
        factory.setPassword(config.getPassword());
        factory.setHost(config.getHost());
        factory.setPort(5672);

        this.connection = factory.newConnection();
        this.threadLocalChannel = new ThreadLocal<>();
    }

}
