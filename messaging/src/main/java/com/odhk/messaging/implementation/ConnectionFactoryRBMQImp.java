package com.odhk.messaging.implementation;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionFactoryRBMQImp {

    // TODO: Use DI to inject MQ Config
    protected static String userName = "admin";
    protected static String password = "admin";
    protected static String host = "127.0.0.1";
    protected static int port = 5672;

    private static ConnectionFactoryRBMQImp instance;
    private Connection connection;
    private ConnectionFactory factory;


    public synchronized static ConnectionFactoryRBMQImp getInstance() throws IOException, TimeoutException{
        if( instance == null){
            instance = new ConnectionFactoryRBMQImp();
        }
        return instance;
    }

    public Connection getConnection(){
        return this.connection;
    }


    private ConnectionFactoryRBMQImp() throws IOException, TimeoutException {
        init();
    }

    private void init() throws IOException, TimeoutException{
        this.factory = new ConnectionFactory();
        this.factory.setUsername(userName);
        this.factory.setPassword(password);
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.connection = this.factory.newConnection();

    }
}
