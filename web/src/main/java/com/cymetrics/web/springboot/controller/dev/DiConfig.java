package com.cymetrics.web.springboot.controller.dev;

import com.cymetrics.persistence.rdbms.dao.dev.JpaWithTransaction;
import com.cymetrics.persistence.rdbms.dao.dev.JpaWithTransactionThreadSafe;
import com.cymetrics.persistence.rdbms.dao.dev.JpaWithoutTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DiConfig {


    @Bean
    public JpaWithoutTransaction jpaWithoutTransactionBean(){
        JpaWithoutTransaction jpaWithoutTransaction = new JpaWithoutTransaction();
        return jpaWithoutTransaction;
    }

    @Bean
    public JpaWithTransaction jpaWithTransactionBean(){
        JpaWithTransaction jpaWithTransaction = new JpaWithTransaction();
        return jpaWithTransaction;
    }

    @Bean
    public JpaWithTransactionThreadSafe jpaWithTransactionThreadSafeBean(){
        JpaWithTransactionThreadSafe jpaWithTransactionThreadSafe = new JpaWithTransactionThreadSafe();
        return jpaWithTransactionThreadSafe;
    }
}
