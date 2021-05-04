package com.cymetrics.domain.scheduling.services;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessageConsumer;
import com.cymetrics.domain.messaging.types.JsonMessage;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.interfaces.IScheduledTask;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class ScheduleService {

    @Inject
    ScheduleRepository repo;
    @Inject
    IMessageConsumer consumer;

    private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    public Optional<String> registerTask(IScheduledTask task, String command)  {
        return consumer.consume(command, new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                task.run(jsonMessage.getJSON());
            }
        });
    }

    public void removeTask(String tag) {
        consumer.removeCallback(tag);
    }

    public Optional<Schedule> createSchedule(String name, String command, String cronExpression){
        Schedule schedule;
        Optional<Schedule> repoSchedule;
        try {
            schedule = new Schedule(name, command, cronExpression);
            repoSchedule = repo.save(schedule);
        } catch (InvalidCronException e){
            logger.error("Schedule cron expression invalid"+e.toString());
            return Optional.empty();
        }
        return repoSchedule;
    }

    public void removeSchedule(String name) {
        repo.deleteByName(name);
    }

    public List<Schedule> getAllSchedules(){
        return repo.getAll();
    }

}
