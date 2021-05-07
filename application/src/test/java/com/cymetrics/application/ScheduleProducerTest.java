package com.cymetrics.application;

import com.cymetrics.application.exception.RetryExceedLimitException;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.services.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    ArrayList<Schedule> mockSchedules;
    static final long TEST_OVERHEAD_LIMIT = 1000;

    @BeforeEach
    public void initMockSchedule() {
        scheduleProducer = new ScheduleProducer();
        mockSchedules = new ArrayList<>();
        mockSchedules.add(mock(Schedule.class));
        mockSchedules.add(mock(Schedule.class));
        scheduleService = mock(ScheduleService.class);
        logger = mock(Logger.class);

        scheduleProducer.scheduleService = scheduleService;
        ZonedDateTime timestamp = LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 45).atZone(ZoneId.systemDefault());
        scheduleProducer.clock = Clock.fixed(timestamp.toInstant(), ZoneId.systemDefault());
        ScheduleProducer.logger = logger;
    }

    @Test
    public void producingSchedulingTask() throws ProduceScheduleException {
        long limit = System.currentTimeMillis() + TEST_OVERHEAD_LIMIT;
        when(scheduleService.getAllSchedules()).thenReturn(mockSchedules);
        Thread t = new Thread(() -> {
            scheduleProducer.startProduceScheduling();
        });
        t.start();

        while (t.getState() != Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit) ;
        if (System.currentTimeMillis() >= limit)
            Assertions.fail("producer thread timeout");

        verify(scheduleService).executeSchedule(mockSchedules.get(0));
        verify(scheduleService).executeSchedule(mockSchedules.get(1));

        t.interrupt();
        while (t.getState() == Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit) ;
        while (t.getState() != Thread.State.TIMED_WAITING && System.currentTimeMillis() < limit) ;
        if (System.currentTimeMillis() >= limit)
            Assertions.fail("producer thread timeout");

        verify(scheduleService, times(2)).executeSchedule(mockSchedules.get(0));
        verify(scheduleService, times(2)).executeSchedule(mockSchedules.get(1));
    }

    @Test
    public void producingSchedulingFailed_throwException() throws ProduceScheduleException {
        doThrow(new ProduceScheduleException("")).when(scheduleService).executeSchedule(mockSchedules.get(0));

        Assertions.assertThrows(RetryExceedLimitException.class, () -> {
            scheduleProducer.executeSchedule(mockSchedules.get(0));
        });
        verify(scheduleService, times(6)).executeSchedule(mockSchedules.get(0));
    }

}
