package com.cymetrics.domain.scheduling.services;

import com.cymetrics.domain.messaging.IMessageConsumer;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.*;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    ScheduleRepository repo;
    @Mock
    IMessageConsumer consumer;
    @Mock
    Logger logger;

    @InjectMocks
    ScheduleService service;

    ArrayList<Schedule> mockSchedules;

    @BeforeEach
    public void init(){
        mockSchedules = new ArrayList<>();
        ZonedDateTime timestamp = LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 45).atZone(ZoneId.systemDefault());
        service.clock = Clock.fixed( timestamp.toInstant(), ZoneId.systemDefault() );
    }

    @Test
    public void createSchedule() throws InvalidCronException {
        when(repo.save(any())).thenReturn(java.util.Optional.of(new Schedule("name0", "command0", "5 * * * *")));

        Optional<Schedule> schedule = service.createSchedule("name0","command0","5 * * * *");
        if(schedule.isPresent())
            Assertions.assertEquals(schedule.get().getName(), "name0");
        else
            Assertions.fail("schedule should not be null");

        schedule = service.createSchedule("name0","command0","100 * * * *");
        Assertions.assertTrue(schedule.isEmpty());
    }

    @Test
    public void executeSchedule() throws ProduceScheduleException {
        mockSchedules.add(mock(Schedule.class));
        mockSchedules.add(mock(Schedule.class));
        when(mockSchedules.get(0).shouldExecute(service.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);
        when(mockSchedules.get(1).shouldExecute(service.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(false);
        when(mockSchedules.get(0).getLastExecutionTime()).thenReturn(null);
        when(mockSchedules.get(1).getLastExecutionTime()).thenReturn(null);

        service.executeSchedule(mockSchedules.get(0));
        service.executeSchedule(mockSchedules.get(1));

        Mockito.verify(mockSchedules.get(0)).produceTask();
        Mockito.verify(mockSchedules.get(1), never()).produceTask();
    }

    @Test
    public void executeFailed() throws ProduceScheduleException {
        mockSchedules.add(mock(Schedule.class));
        doThrow(new ProduceScheduleException("")).when(mockSchedules.get(0)).produceTask();
        when(mockSchedules.get(0).getLastExecutionTime()).thenReturn(null);
        when(mockSchedules.get(0).shouldExecute(service.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);

        Assertions.assertThrows(Exception.class,()->
            service.executeSchedule(mockSchedules.get(0))
        );
    }

    @Test
    public void executeDelayed_ReProduce() throws ProduceScheduleException {
        mockSchedules.add(mock(Schedule.class));
        mockSchedules.add(mock(Schedule.class));
        ZonedDateTime timestamp1 = LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 25).atZone(ZoneId.systemDefault());
        ZonedDateTime timestamp2 = LocalDateTime.of(2019, Month.MARCH, 27, 10, 4, 25).atZone(ZoneId.systemDefault());
        when(mockSchedules.get(0).getLastExecutionTime()).thenReturn(timestamp1);
        when(mockSchedules.get(1).getLastExecutionTime()).thenReturn(timestamp2);
        when(mockSchedules.get(1).shouldExecute(service.clock.instant().atZone(ZoneId.systemDefault()))).thenReturn(true);

        service.executeSchedule(mockSchedules.get(0));
        service.executeSchedule(mockSchedules.get(1));

        Mockito.verify(mockSchedules.get(0), never()).produceTask();
        Mockito.verify(mockSchedules.get(1)).produceTask();
    }

}
