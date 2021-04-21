package com.cymetrics.domain.scheduling.service;

import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import com.cymetrics.domain.scheduling.interfaces.IScheduledTask;

public class ScheduleService {

    public void register(IScheduledTask task, String taskName, String cronExpression, String[] args) throws InvalidCronException {

    }

    public void remove(String taskName) {

    }

    // Running Scheduling service Entry Point
    public static void main(String[] args) {

    }

}
