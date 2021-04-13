package com.odhk.messaging.implementation;

import com.odhk.messaging.IMessageQueueProxy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.exceptions.QueueLifecycleException;


public class MessageProxyRBMQImp implements IMessageQueueProxy {

    private  Connection connection;
    protected Channel channel;
    protected CopyOnWriteArrayList<String> queueNames = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<String> exchangeNames = new CopyOnWriteArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(MessageProxyRBMQImp.class);

    public MessageProxyRBMQImp() throws QueueLifecycleException {
        try {
            ConnectionFactoryRBMQImp connectionPool = ConnectionFactoryRBMQImp.getInstance();
            this.connection = connectionPool.getConnection();
            this.channel = this.connection.createChannel();
        } catch (IOException | TimeoutException e){
            throw new QueueLifecycleException(e.getMessage());
        }
    }

    @Override
    public void createQueue(String name) throws QueueLifecycleException{
        try {
            this.channel.queueDeclare(name, false, false, false, null);
        } catch(IOException  e){
            logger.error("create queue error: "+e);
            throw new QueueLifecycleException(e.getMessage());
        }
        this.queueNames.add(name);
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
    public void deleteQueue(String name) throws QueueLifecycleException {
        try{
            this.channel.queueDelete(name);
        } catch(IOException  e){
            logger.error("delete queue error:"+e);
            throw new QueueLifecycleException(e.getMessage());
        }
    }

    @Override
    public void cleanQueue(String name) throws QueueLifecycleException {
        try{
            this.channel.queuePurge(name);
        } catch(IOException  e){
            logger.error("clean queue error:"+e);
            throw new QueueLifecycleException(e.getMessage());
        }
    }

    @Override
    public void createTopic(String name) throws QueueLifecycleException{
        try {
           this.channel.exchangeDeclare(name, "fanout");
        } catch(IOException  e){
            logger.error("create exchange error:"+name);
            throw new QueueLifecycleException(e.getMessage());
        }
        this.exchangeNames.add(name);
    }

    @Override
    public void deleteTopic(String name) throws QueueLifecycleException{
        try{
            this.channel.exchangeDelete(name);
        } catch(IOException e){
            logger.error("delete exchange error:"+e);
            throw new QueueLifecycleException(e.getMessage());
        }
    }

    public ArrayList<String> getQueueList(){
        ArrayList<String> queueList = new ArrayList<>();
        queueList.addAll(queueNames);
        return queueList;
    }

    public ArrayList<String> getExchangeList(){
        ArrayList<String> exchangeList = new ArrayList<>();
        exchangeList.addAll(queueNames);
        return exchangeList;
    }

    protected byte[] EncodeObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }


    protected Object DecodeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // TODO: until message objects have been defined, we cannot create white list
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }
}
