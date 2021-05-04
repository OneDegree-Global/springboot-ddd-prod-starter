package com.cymetrics.domain.scheduling.services;

import com.cymetrics.domain.messaging.IMessageConsumer;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    ScheduleRepository repo;
    @Mock
    IMessageConsumer consumer;

    @InjectMocks
    ScheduleService service;

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

}
