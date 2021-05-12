package com.cymetrics.web.springboot.config.di;

import com.cymetrics.messaging.implementation.rbmq.ChannelFactory;
import com.cymetrics.messaging.implementation.rbmq.RBMQConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Configuration
public class Messaging {

    @Bean("rabbitmqConfig")
    public RBMQConfig rbmqConfigBean()  {

        RBMQConfig config = new RBMQConfig(System.getProperty("RBMQ_USER"),
                System.getProperty("RBMQ_PASSWORD"),
                System.getProperty("RBMQ_HOST"),
                5672);
        return config;
    }

}
