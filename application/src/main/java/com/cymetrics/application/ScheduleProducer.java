package com.cymetrics.application;

import com.cymetrics.application.aspect.annotations.Retry;
import com.cymetrics.application.services.UserService;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import com.cymetrics.domain.scheduling.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ScheduleProducer {

    static ScheduleService scheduleService;
    private static Logger logger = LoggerFactory.getLogger(ScheduleProducer.class);

    static Clock clock = Clock.systemDefaultZone();

    public static void main(String[] args) {
        while(true){
            ArrayList<Schedule> schedules = scheduleService.getAllSchedules();
            for(Schedule schedule : schedules){
                try {
                    produceTask(schedule);
                } catch(ProduceScheduleException e){
                    logger.error("produce scheduled task failed:"+e.toString());
                }
            }
            int secondsToNextMinute = 60 - clock.instant().atZone(ZoneId.systemDefault()).getSecond();
            try {
                Thread.sleep(secondsToNextMinute);
            } catch( InterruptedException e){
                Thread.currentThread().interrupt();
                logger.info("Schedule task producer sleep got interrupted:"+e.toString());
            }
        }
    }

    @Retry(baseInterval = 6 , retries = 5)
    public static void produceTask(Schedule s) throws ProduceScheduleException {
        s.produceTask();
    }
}
