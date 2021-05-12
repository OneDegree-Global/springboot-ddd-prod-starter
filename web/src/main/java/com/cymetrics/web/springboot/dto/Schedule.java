package com.cymetrics.web.springboot.dto;
import lombok.Data;

@Data
public class Schedule {
    private final String name;
    private final String command;
    private final String cronExpression;
}
