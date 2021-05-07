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

import java.time.*;
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
        ZonedDateTime timestamp = LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 45).atZone(ZoneId.systemDefault());
        scheduleProducer.clock = Clock.fixed( timestamp.toInstant(), ZoneId.systemDefault() );
        ScheduleProducer.logger = logger;
    }

    @Test
    public void executeSchedule() throws ProduceScheduleException {
        long limit = System.currentTimeMillis() + 1000;
        when(scheduleService.getAllSchedules()).thenReturn(mockSchedules);
        when(mockSchedules.get(0).shouldExecute(scheduleProducer.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);
        when(mockSchedules.get(1).shouldExecute(scheduleProducer.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);
        when(mockSchedules.get(0).getLastExecutionTime()).thenReturn(null);
        when(mockSchedules.get(1).getLastExecutionTime()).thenReturn(null);
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

    @Test
    public void executeDelayed_ReProduce() throws ProduceScheduleException {
        long limit = System.currentTimeMillis() + 1000;
        ZonedDateTime timestamp1 = LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 25).atZone(ZoneId.systemDefault());
        ZonedDateTime timestamp2 = LocalDateTime.of(2019, Month.MARCH, 27, 10, 4, 25).atZone(ZoneId.systemDefault());
        when(scheduleService.getAllSchedules()).thenReturn(mockSchedules);
        when(mockSchedules.get(0).getLastExecutionTime()).thenReturn(timestamp1);
        when(mockSchedules.get(1).getLastExecutionTime()).thenReturn(timestamp2);
        when(mockSchedules.get(1).shouldExecute(scheduleProducer.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);

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
        Mockito.verify(mockSchedules.get(0), never()).produceTask();
        Mockito.verify(mockSchedules.get(1)).produceTask();

    }

}
