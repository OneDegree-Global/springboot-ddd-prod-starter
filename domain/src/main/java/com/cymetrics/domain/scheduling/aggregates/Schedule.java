package com.cymetrics.domain.scheduling.aggregates;

import com.cymetrics.domain.scheduling.aggregates.VO.CronExpression;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.exception.ProduceScheduleException;

import com.cymetrics.domain.messaging.IMessageProducer;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.messageTypes.JSONMessage;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.inject.Inject;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
public class Schedule {

    @Inject
    IMessageProducer producer;

    CronExpression cronExpression;
    String name;
    String command;

    @Setter
    String[] args;
    @Setter
    boolean isActive;
    @Setter
    boolean isOverwrite; // task will overwrite other all task with same command in the queue
    @Setter
    ZonedDateTime effectiveTime; // task will not be invoked if after this time

    Clock clock = Clock.system(ZoneId.systemDefault());

    public Schedule(String name, String command, String expression) throws InvalidCronException {
        this.cronExpression = new CronExpression(expression);
        this.name = name;
        this.command = command;
        this.isActive = true;
        this.effectiveTime = null;
        this.args = null;
    }

    public boolean shouldExecute(){
        if(!this.isActive || clock.instant().isAfter(this.effectiveTime.toInstant()))
            return false;
        if(this.cronExpression.isMatch(clock.instant().atZone(ZoneId.systemDefault())))
            return true;
        return false;
    }

    public void produceTask () throws ProduceScheduleException {
        JSONObject json = new JSONObject();
        for (String arg : args) {
            String key = arg.split("=")[0];
            String value = arg.split("=")[1];
            json.put(key, value);
        }
        try {
            producer.send("schedule-"+command, new JSONMessage(json));
        } catch(ProtocolIOException e){
            throw new ProduceScheduleException("Produce schedule task error:"+e.toString());
        }
    }

}
