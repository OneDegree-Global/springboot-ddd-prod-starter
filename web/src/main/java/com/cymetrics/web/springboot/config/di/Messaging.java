package com.cymetrics.web.springboot.config.di;

import com.cymetrics.messaging.implementation.rbmq.ChannelFactory;
import com.cymetrics.messaging.implementation.rbmq.RBMQConfig;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Configuration
public class Messaging {

    @Value("${rbmq.user}")
    private String user;

    @Value("${rbmq.password}")
    private String password;

    @Value("${rbmq.host}")
    private String host;

    @Bean("rabbitmqConfig")
    public RBMQConfig rbmqConfigBean()  {

        if( user == null || password == null ||  host == null)
            throw new NullArgumentException("environment var required to start rbqm service not provided");

        RBMQConfig config = new RBMQConfig(user,
                password,
                host,
                5672);
        return config;
    }

}
