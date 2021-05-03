package com.cymetrics.application;

import com.cymetrics.application.aspect.annotations.Retry;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLOutput;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;

public class ScheduleProducer {

    @Inject
    ScheduleService scheduleService;
    Clock clock = Clock.systemDefaultZone();
    static Logger logger = LoggerFactory.getLogger(ScheduleProducer.class);
    static ScheduleProducer scheduleProducer;

    public ScheduleProducer(){
    }

    public void startProduceScheduling() throws ProduceScheduleException {

        while(true){
            ArrayList<Schedule> schedules = scheduleService.getAllSchedules();
            for(Schedule schedule : schedules){
                try {
                    System.out.println("wwww");
                    this.produceTask(schedule);
                    System.out.println("vvvv");
                } catch(ProduceScheduleException e){
                    System.out.println("thrownnn");
                    logger.error("produce scheduled task failed:"+e.toString());
                }
            }
            int secondsToNextMinute = 60 - clock.instant().atZone(ZoneId.systemDefault()).getSecond();
            try {
                Thread.sleep(secondsToNextMinute);
            } catch( InterruptedException e){
                logger.info("Schedule task producer sleep got interrupted:"+e.toString());
            }
        }
    }

    public static void main(String[] args) throws ProduceScheduleException {
        scheduleProducer = new ScheduleProducer();
        scheduleProducer.startProduceScheduling();
    }

    @Retry
    public void produceTask(Schedule s) throws ProduceScheduleException {
        System.out.println("qq");
        s.produceTask();
        System.out.println("qq2");

    }
}
