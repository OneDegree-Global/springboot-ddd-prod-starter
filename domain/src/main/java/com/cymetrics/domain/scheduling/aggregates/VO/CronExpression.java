package com.cymetrics.domain.scheduling.aggregates.VO;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import lombok.Getter;

import com.cronutils.model.Cron;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

@Getter
public class CronExpression {

    Cron cron;

    public CronExpression(String expression) throws InvalidCronException {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        cron = parser.parse(expression);
        if(cron == null){
            throw new InvalidCronException("Cron expression invalid! " + expression );
        }
    }

    public String getDescription(){
        return CronDescriptor.instance(Locale.TAIWAN).describe(this.cron);
    }

    public String getStringExpression(){
        return cron.asString();
    }
    public Optional<ZonedDateTime> getNextExecutionTime(ZonedDateTime instant){
        return ExecutionTime.forCron(this.cron).nextExecution(instant);
    }

    public boolean isMatch(ZonedDateTime instant){
        return ExecutionTime.forCron(cron).isMatch(instant);
    }

}
