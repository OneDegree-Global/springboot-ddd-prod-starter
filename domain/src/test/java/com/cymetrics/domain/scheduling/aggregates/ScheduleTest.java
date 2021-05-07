package com.cymetrics.domain.scheduling.aggregates;

import com.cymetrics.domain.messaging.IMessageProducer;
import com.cymetrics.domain.messaging.types.JsonMessage;
import com.cymetrics.domain.scheduling.exception.InvalidCronException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
public class ScheduleTest {

    @Mock
    IMessageProducer mockedProducer;

    @Test
    public void construct() throws InvalidCronException {
        Schedule s = new Schedule("name0", "command0", "5 10 * * *");
        ZonedDateTime timestamp =
                LocalDateTime.of(2019, Month.MARCH, 28, 1, 5, 25).atZone(ZoneId.systemDefault());
        ZonedDateTime expectTimestamp =
                LocalDateTime.of(2019, Month.MARCH, 28, 10, 5, 25).atZone(ZoneId.systemDefault());
        s.clock = Clock.fixed(timestamp.toInstant(), ZoneId.systemDefault());

        Assertions.assertEquals("name0", s.getName());
        Assertions.assertEquals("command0", s.getCommand());
        Assertions.assertEquals("5 10 * * *", s.getCronExpression().getStringExpression());
        Assertions.assertEquals("at 10:05", s.getCronExpression().getDescription());
        Assertions.assertEquals(expectTimestamp.truncatedTo(ChronoUnit.MINUTES), s.getCronExpression().getNextExecutionTime(timestamp).get().truncatedTo(ChronoUnit.MINUTES));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5 30 * * *",
            "5 * * 13 *",
            "5 * 32 * *",
            "* * * * * * *",
    })
    public void construct_throwInvalidCron(String cron) {
        Assertions.assertThrows(InvalidCronException.class, () -> {
            Schedule s = new Schedule("name1", "command1", cron);
        });
    }

    @Test
    public void shouldExecute() throws InvalidCronException {
        Schedule s1 = new Schedule("name2", "command2", "5 10 * * *");
        Schedule s2 = new Schedule("name3", "command3", "5 10 * * *");
        Schedule s3 = new Schedule("name4", "command4", "5 10 * * *");
        Schedule s4 = new Schedule("name5", "command5", "5 10 * * *");
        Schedule s5 = new Schedule("name6", "command6", "5 10 * * *");

        Instant testInstant1 =
                LocalDateTime.of(2019, Month.MARCH, 28, 1, 5, 25).atZone(ZoneId.systemDefault()).toInstant();
        Instant testInstant2 =
                LocalDateTime.of(2019, Month.MARCH, 28, 10, 5, 45).atZone(ZoneId.systemDefault()).toInstant();

        s1.clock = Clock.fixed(testInstant1, ZoneId.systemDefault());
        s2.clock = Clock.fixed(testInstant2, ZoneId.systemDefault());
        s3.clock = Clock.fixed(testInstant2, ZoneId.systemDefault());
        s4.clock = Clock.fixed(testInstant2, ZoneId.systemDefault());
        s5.clock = Clock.fixed(testInstant2, ZoneId.systemDefault());

        s3.setActive(false);
        s4.setEffectiveTime(LocalDateTime.of(2019, Month.MARCH, 29, 10, 5, 45).atZone(ZoneId.systemDefault()));
        s5.setEffectiveTime(LocalDateTime.of(2019, Month.MARCH, 26, 10, 5, 45).atZone(ZoneId.systemDefault()));

        Assertions.assertEquals(false, s1.shouldExecute());
        Assertions.assertEquals(true, s2.shouldExecute());
        Assertions.assertEquals(false, s3.shouldExecute());
        Assertions.assertEquals(true, s4.shouldExecute());
        Assertions.assertEquals(false, s5.shouldExecute());

    }

    @Test
    public void produceTask() throws Exception {
        Schedule s = new Schedule("name5", "command5", "5 10 * * *");
        s.producer = mockedProducer;
        s.setArgs("a=1 b=2 c=3".split(" "));

        s.produceTask();
        JSONObject json = new JSONObject();
        json.put("a", "1");
        json.put("b", "2");
        json.put("c", "3");
        JsonMessage args = new JsonMessage(json);

        Mockito.verify(s.producer, Mockito.times(1)).send("command5", args);
    }

    @Test
    public void needReProduce() throws Exception {
        Instant testInstant =
                LocalDateTime.of(2019, Month.MARCH, 28, 11, 5, 25).atZone(ZoneId.systemDefault()).toInstant();
        Schedule s1 = new Schedule("name6", "command6", "5 10 * * *");
        Schedule s2 = new Schedule("name7", "command7", "5 10 * * *");
        s1.setReProducible(true);
        s2.setReProducible(true);
        s1.clock = Clock.fixed(testInstant, ZoneId.systemDefault());
        s2.clock = Clock.fixed(testInstant, ZoneId.systemDefault());

        s1.setLastExecutionTime(LocalDateTime.of(2019, Month.MARCH, 27, 10, 5, 45).atZone(ZoneId.systemDefault()));
        s2.setLastExecutionTime(LocalDateTime.of(2019, Month.MARCH, 28, 10, 5, 45).atZone(ZoneId.systemDefault()));

        Assertions.assertTrue(s1.needReProduce());
        Assertions.assertFalse(s2.needReProduce());
    }

}
