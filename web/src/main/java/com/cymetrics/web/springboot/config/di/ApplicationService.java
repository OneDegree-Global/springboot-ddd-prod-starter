package com.cymetrics.web.springboot.config.di;


import com.cymetrics.application.aspect.AuthorizeAspect;
import com.cymetrics.application.services.LoginService;
import com.cymetrics.application.services.UserService;
import com.cymetrics.domain.messaging.*;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.rbmq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class ApplicationService {

    private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Bean
    public AuthorizeAspect authorizeAspectBean(){
        AuthorizeAspect authorizeAspect = new AuthorizeAspect();
        return authorizeAspect;
    }


    @Bean
    public UserService userServiceBean() {
        UserService userService = new UserService();
        return userService;
    }

    @Bean
    public ScheduleService scheduleServiceBean() {
        ScheduleService scheduleService = new ScheduleService();
        return scheduleService;
    }

    @Bean
    public LoginService loginServiceBean(){
        LoginService loginService = new LoginService();
        return loginService;
    }

    @Bean
    @Scope("prototype")
    public IMessageConsumer messageConsumerBean() throws QueueLifecycleException{
        try {
            IMessageConsumer consumer = new MessageConsumerRBMQImp();
            return consumer;
        } catch(ProtocolIOException | QueueLifecycleException e){
            logger.error("instantiate MessageConsumer failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessageConsumer failed");
        }
    }

    @Bean
    @Scope("prototype")
    public IMessageProducer messageProducerBean() throws QueueLifecycleException{
        try {
            IMessageProducer producer = new MessageProducerRBMQImp();
            return producer;
        } catch(ProtocolIOException | QueueLifecycleException e){
            logger.error("instantiate MessageProducer failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessageProducer failed");
        }
    }

    @Bean
    @Scope("prototype")
    public IMessagePublisher messagePublisherBean() throws QueueLifecycleException{
        try {
            IMessagePublisher publisher = new MessagePublisherRBMQImp();
            return publisher;
        } catch(QueueLifecycleException e){
            logger.error("instantiate MessagePublisher failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessagePublisher failed");
        }
    }

    @Bean
    @Scope("prototype")
    public IMessageSubscriber messageSubscriberBean() throws QueueLifecycleException{
        try {
            IMessageSubscriber subscriber = new MessageSubscriberRBMQImp();
            return subscriber;
        } catch(ProtocolIOException | QueueLifecycleException e){
            logger.error("instantiate MessageSubscriber failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessageSubscriber failed");
        }
    }

    @Bean
    @Scope("prototype")
    public IMessageQueueProxy messageQueueProxyBean() throws QueueLifecycleException{
        try {
            IMessageQueueProxy proxy = new MessageProxyRBMQImp();
            return proxy;
        } catch( QueueLifecycleException e){
            logger.error("instantiate MessageQueue Proxy failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessageQueue Proxy failed");
        }
    }

}