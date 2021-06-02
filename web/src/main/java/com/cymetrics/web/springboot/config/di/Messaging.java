package com.cymetrics.web.springboot.config.di;

import com.cymetrics.domain.messaging.*;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.rbmq.*;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;


@Configuration
public class Messaging {

    private static Logger logger = LoggerFactory.getLogger(Messaging.class);
    @Value("${rbmq.user}")
    private String user;
    @Value("${rbmq.password}")
    private String password;
    @Value("${rbmq.host}")
    private String host;
    @Value("${rbmq.launch}")
    private boolean launch;

    @Bean("rabbitmqConfig")
    public RBMQConfig rbmqConfigBean() {

        if (user == null || password == null || host == null)
            throw new NullArgumentException("environment var required to start rbqm service not provided");

        RBMQConfig config = new RBMQConfig(user,
                password,
                host,
                5672);
        ChannelFactory.setConfig(config);

        return config;
    }

    @Bean(value = "mqConsumer")
    @Scope("prototype")
    @DependsOn("rabbitmqConfig")
    public IMessageConsumer messageConsumerBean() throws QueueLifecycleException {
        try {
            IMessageConsumer consumer = new MessageConsumerRBMQImp();
            return consumer;
        } catch (ProtocolIOException | QueueLifecycleException e) {
            logger.error("instantiate MessageConsumer failed:" + e.toString());
            throw new QueueLifecycleException("instantiate MessageConsumer failed");
        }
    }

    @Bean(value = "mqProducer")
    @Scope("prototype")
    @DependsOn("rabbitmqConfig")
    public IMessageProducer messageProducerBean() throws QueueLifecycleException {
        try {
            IMessageProducer producer = new MessageProducerRBMQImp();
            return producer;
        } catch (ProtocolIOException | QueueLifecycleException e) {
            logger.error("instantiate MessageProducer failed:" + e.toString());
            throw new QueueLifecycleException("instantiate MessageProducer failed");
        }
    }

    @Bean(value = "mqPublisher")
    @Scope("prototype")
    @DependsOn("rabbitmqConfig")
    public IMessagePublisher messagePublisherBean() throws QueueLifecycleException {
        try {
            IMessagePublisher publisher = new MessagePublisherRBMQImp();
            return publisher;
        } catch (QueueLifecycleException e) {
            logger.error("instantiate MessagePublisher failed:" + e.toString());
            throw new QueueLifecycleException("instantiate MessagePublisher failed");
        }
    }

    @Bean(value = "mqSubscriber")
    @Scope("prototype")
    @DependsOn("rabbitmqConfig")
    public IMessageSubscriber messageSubscriberBean() throws QueueLifecycleException {
        try {
            IMessageSubscriber subscriber = new MessageSubscriberRBMQImp();
            return subscriber;
        } catch (ProtocolIOException | QueueLifecycleException e) {
            logger.error("instantiate MessageSubscriber failed:" + e.toString());
            throw new QueueLifecycleException("instantiate MessageSubscriber failed");
        }
    }

    @Bean(value = "mqProxy")
    @Scope("prototype")
    @DependsOn("rabbitmqConfig")
    public IMessageQueueProxy messageQueueProxyBean() throws QueueLifecycleException {
        try {
            IMessageQueueProxy proxy = new MessageProxyRBMQImp();
            return proxy;
        } catch (QueueLifecycleException e) {
            logger.error("instantiate MessageQueue Proxy failed:" + e.toString());
            throw new QueueLifecycleException("instantiate MessageQueue Proxy failed");
        }
    }

}
