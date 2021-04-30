package com.cymetrics.web.springboot.config.di;


import com.cymetrics.application.aspect.AuthorizeAspect;
import com.cymetrics.application.services.LoginService;
import com.cymetrics.application.services.UserService;
import com.cymetrics.domain.scheduling.service.ScheduleService;
import com.cymetrics.messaging.IMessageConsumer;
import com.cymetrics.messaging.IMessageProducer;
import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.messaging.implementation.rbmq.MessageConsumerRBMQImp;
import com.cymetrics.messaging.implementation.rbmq.MessageProducerRBMQImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.query.QueryCreationException;


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
            logger.error("instantiate MessageProducer failed:"+e.toString());
            throw new QueueLifecycleException("instantiate MessageProducer failed");
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

}