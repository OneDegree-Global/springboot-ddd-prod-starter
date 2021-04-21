package com.cymetrics.domain.scheduling.aggregates;

import com.cymetrics.domain.scheduling.aggregates.VO.CronExpression;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.interfaces.IScheduledTask;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class Scheduler {

    IScheduledTask task;
    CronExpression cronExpression;
    String name;
    boolean isActive;
    Instant effectiveTime; // task will not be invoked if after this time
    String[] args;

    public Scheduler() throws InvalidCronException {

    }

    public Instant getNextExecutionTime(){
        return null;
    }

    public boolean shouldExecute(){
        return true;
    }

    public void execute throws(){}

}
