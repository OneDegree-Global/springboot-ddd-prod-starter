package com.cymetrics.domain.scheduling.aggregates.VO;

import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import lombok.Getter;

@Getter
public class CronExpression {

    private final String seconds = "";
    private final String minutes = "";
    private final String hours = "";
    private final String dayOfMonth = "";
    private final String month = "";
    private final String dayOfWeek = "";
    private final String year = "";
    public CronExpression(String expression) throws InvalidCronException {

    }

}
