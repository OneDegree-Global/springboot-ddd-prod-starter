package com.cymetrics.domain.scheduling.services;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessageConsumer;
import com.cymetrics.domain.messaging.types.JsonMessage;
import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;
import com.cymetrics.domain.scheduling.interfaces.IScheduledTask;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


public class ScheduleService {

    @Inject
    ScheduleRepository repo;
    @Inject
    IMessageConsumer consumer;

    private static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    Clock clock = Clock.systemDefaultZone();


    public Optional<String> registerTask(IScheduledTask task, String command) {
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

    public Optional<Schedule> createSchedule(String name, String command, String cronExpression) {
        Schedule schedule;
        Optional<Schedule> repoSchedule;
        try {
            schedule = new Schedule(name, command, cronExpression);
            repoSchedule = repo.save(schedule);
        } catch (InvalidCronException e) {
            logger.error("Schedule cron expression invalid" + e.toString());
            return Optional.empty();
        }
        return repoSchedule;
    }

    public void executeSchedule(Schedule schedule) throws ProduceScheduleException {
        ZonedDateTime timestamp = clock.instant().atZone(ZoneId.systemDefault());

        // Prevent task to be produced twice in the same timeslot if exception occurs
        if (schedule.getLastExecutionTime() != null &&
                schedule.getLastExecutionTime().truncatedTo(ChronoUnit.MINUTES).isEqual(timestamp.truncatedTo(ChronoUnit.MINUTES))) {
            return;
        }
        if (schedule.shouldExecute(timestamp) || schedule.needReProduce()) {
            schedule.setLastExecutionTime(clock.instant().atZone(ZoneId.systemDefault()));
            saveSchedule(schedule);
            schedule.produceTask();
        }

    }

    public void removeSchedule(String name) {
        repo.deleteByName(name);
    }

    public void saveSchedule(Schedule schedule) {
        repo.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return repo.getAll();
    }

}
