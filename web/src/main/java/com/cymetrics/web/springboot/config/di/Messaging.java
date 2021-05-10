package com.cymetrics.web.springboot.config.di;

import com.cymetrics.messaging.implementation.rbmq.RBMQConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Messaging {
    @Bean
    public RBMQConfig rbmqConfigBean(){
        RBMQConfig config = new RBMQConfig("admin",
                "admin",
                "127.0.0.1",
                5672);
        return config;
    }
}
