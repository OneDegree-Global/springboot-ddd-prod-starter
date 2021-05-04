package com.cymetrics.application;

import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleProducerTest {

    ScheduleProducer scheduleProducer;

    ScheduleService scheduleService;
    Logger logger;
    ArrayList<Schedule> mockSchedules = new ArrayList<>();


    @BeforeEach
    public void initMockSchedule(){
        scheduleProducer = new ScheduleProducer();
        mockSchedules = new ArrayList<>();
        mockSchedules.add(mock(Schedule.class));
        mockSchedules.add(mock(Schedule.class));
        scheduleService = mock(ScheduleService.class);
        logger = mock(Logger.class);

        scheduleProducer.scheduleService = scheduleService;
        scheduleProducer.clock = Clock.fixed( Instant.ofEpochMilli(1000000000) , ZoneId.systemDefault() );
        ScheduleProducer.logger = logger;
    }

    @Test
    public void executeSchedule() throws ProduceScheduleException {
        long limit = System.currentTimeMillis() + 1000;
        when(scheduleService.getAllSchedules()).thenReturn(mockSchedules);
        Thread t = new Thread( ()->{
            try {
                scheduleProducer.startProduceScheduling();
            } catch (ProduceScheduleException e) {
                e.printStackTrace();
            }
        });

        t.start();

        while(t.getState() != Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit );
        if(System.currentTimeMillis() >= limit)
            Assertions.fail("producer thread timeout");
        Mockito.verify(mockSchedules.get(0)).produceTask();
        Mockito.verify(mockSchedules.get(1)).produceTask();
        t.interrupt();

        while(t.getState() == Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit );
        while(t.getState() != Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit );
        if(System.currentTimeMillis() >= limit)
            Assertions.fail("producer thread timeout");
        Mockito.verify(mockSchedules.get(0), Mockito.times(2)).produceTask();
        Mockito.verify(mockSchedules.get(1), Mockito.times(2)).produceTask();

    }

    @Test
    public void executeFailed() throws ProduceScheduleException {
        doThrow(new ProduceScheduleException("")).when(mockSchedules.get(0)).produceTask();

        Assertions.assertThrows(Exception.class,()->
            scheduleProducer.produceTask(mockSchedules.get(0))
        );
        Mockito.verify(mockSchedules.get(0), Mockito.times(6)).produceTask();
    }


}
