package com.odhk.messaging.implementation;


import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionFactory {

    // TODO: Use DI to inject MQ Config
    protected static String userName = "admin";
    protected static String password = "admin";
    protected static String host = "127.0.0.1";
    protected static int port = 5672;

    private static ConnectionFactory instance;
    private Connection connection;

    public synchronized static ConnectionFactory createInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = new ConnectionFactory();
        }
        return instance;
    }

    public static ConnectionFactory getInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = createInstance();
        }
        return instance;
    }

    public Connection getConnection(){
        return this.connection;
    }


    private ConnectionFactory() throws IOException, TimeoutException {
        init();
    }

    private void init() throws IOException, TimeoutException{
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        this.connection = factory.newConnection();

    }
}
