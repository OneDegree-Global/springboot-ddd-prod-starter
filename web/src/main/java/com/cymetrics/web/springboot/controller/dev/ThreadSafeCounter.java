package com.cymetrics.web.springboot.controller.dev;

import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadSafeCounter {
    private AtomicInteger count = new AtomicInteger(0);

    @Inject
    public ThreadSafeCounter(){

    }

    public int incCount(){
        return count.incrementAndGet();
    }
}
