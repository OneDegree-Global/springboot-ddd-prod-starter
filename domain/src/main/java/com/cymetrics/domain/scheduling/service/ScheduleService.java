package com.cymetrics.domain.scheduling.service;

import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.interfaces.IScheduledTask;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import com.cymetrics.messaging.IMessageCallback;
import com.cymetrics.messaging.IMessageConsumer;
import com.cymetrics.messaging.messageTypes.JSONMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
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
                JSONMessage jsonMessage = (JSONMessage) message;
                task.run(jsonMessage.getJSON());
            }
        });
    }

    public void removeTask(String tag) {
        consumer.removeCallback(tag);
    }

    public Optional<Schedule> createSchedule(String name, String command, String cronExpression){
        Schedule schedule;
        try {
            schedule = new Schedule(name, command, cronExpression);
            Optional<Schedule> repoSchedule = repo.save(schedule);
        } catch (InvalidCronException e){
            logger.error("Schedule cron expression invalid"+e.toString());
            return Optional.empty();
        }
        return Optional.of(schedule);
    }

    public void removeSchedule(String name) {
        repo.deleteByName(name);
    }

    public ArrayList<Schedule> getAllSchedules(){
        return repo.getAll();
    }

}
