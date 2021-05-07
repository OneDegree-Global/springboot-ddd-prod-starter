package com.cymetrics.application;

import com.cymetrics.application.aspect.annotations.Retry;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;

public class ScheduleProducer {

    @Inject
    ScheduleService scheduleService;
    Clock clock = Clock.systemDefaultZone();

    static Logger logger = LoggerFactory.getLogger(ScheduleProducer.class);
    static ScheduleProducer scheduleProducer;
    static final int SECONDS_IN_MIN = 60;

    public void startProduceScheduling() {
        while (true) {
            ArrayList<Schedule> schedules = (ArrayList<Schedule>) scheduleService.getAllSchedules();
            for (Schedule schedule : schedules) {
                try {
                    executeSchedule(schedule);
                } catch (ProduceScheduleException e) {
                    logger.error("Produce scheduled task failed:" + e.toString());
                }
            }

            int secondsToNextMinute = SECONDS_IN_MIN - clock.instant().atZone(ZoneId.systemDefault()).getSecond();
            try {
                Thread.interrupted();
                Thread.sleep(secondsToNextMinute);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("Schedule task producer sleep got interrupted:" + e.toString());
            }
        }
    }

    public static void main(String[] args) throws ProduceScheduleException {
        scheduleProducer = new ScheduleProducer();
        scheduleProducer.startProduceScheduling();
    }

    @Retry(baseInterval = 4, retries = 5)
    @Transactional()
    public void executeSchedule(Schedule schedule) throws ProduceScheduleException {
        scheduleService.executeSchedule(schedule);
    }

}
