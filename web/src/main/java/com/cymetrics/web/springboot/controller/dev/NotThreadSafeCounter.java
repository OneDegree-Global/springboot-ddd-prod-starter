package com.cymetrics.web.springboot.controller.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.security.PublicKey;

@Configuration
public class NotThreadSafeCounter {
    private int count = 0;

    @Inject
    public NotThreadSafeCounter(){

    }

    public int incCount(){
        this.count++;
        return this.count;
    }
}
