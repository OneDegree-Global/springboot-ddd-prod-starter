package com.cymetrics.web.springboot.requestbody;
import lombok.Data;


@Data
public class ScheduleRequest {
    private final String command;
    private final String name;
    private final String cronExpression;
}
