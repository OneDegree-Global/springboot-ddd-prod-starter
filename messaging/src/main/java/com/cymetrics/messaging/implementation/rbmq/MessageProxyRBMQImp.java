package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageQueueProxy;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;


public class MessageProxyRBMQImp implements IMessageQueueProxy {

    private Channel channel;
    private CopyOnWriteArrayList<String> queueNames = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> exchangeNames = new CopyOnWriteArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(MessageProxyRBMQImp.class);

    public MessageProxyRBMQImp() throws QueueLifecycleException {
        try {
            this.channel = ChannelFactory.getInstance().getChannel();
        } catch( IOException | TimeoutException e){
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public void createQueue(String name) throws QueueLifecycleException{
        try {
            this.channel.queueDeclare(name, false, false, false, null);
        } catch(IOException  e){
            logger.error("create queue error: "+e);
            throw new QueueLifecycleException(e.toString());
        }
        this.queueNames.add(name);
    }


    @Override
    public void deleteQueue(String name) throws QueueLifecycleException {
        try{
            this.channel.queueDelete(name);
        } catch(IOException  e){
            logger.error("delete queue error:"+e);
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public void cleanQueue(String name) throws QueueLifecycleException {
        try{
            this.channel.queuePurge(name);
        } catch(IOException  e){
            logger.error("clean queue error:"+e);
            throw new QueueLifecycleException(e.toString());
        }
    }

    @Override
    public void createTopic(String name) throws QueueLifecycleException{
        try {
           this.channel.exchangeDeclare(name, "fanout");
        } catch(IOException  e){
            logger.error("create exchange error:"+name);
            throw new QueueLifecycleException(e.toString());
        }
        this.exchangeNames.add(name);
    }

    @Override
    public void deleteTopic(String name) throws QueueLifecycleException{
        try{
            this.channel.exchangeDelete(name);
        } catch(IOException e){
            logger.error("delete exchange error:"+e);
            throw new QueueLifecycleException(e.toString());
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

}
