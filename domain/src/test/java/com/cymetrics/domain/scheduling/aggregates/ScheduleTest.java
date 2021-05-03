package com.cymetrics.domain.scheduling.aggregates;

import com.cymetrics.domain.messaging.IMessageProducer;
import com.cymetrics.domain.messaging.messageTypes.JSONMessage;
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

@ExtendWith(MockitoExtension.class)
public class ScheduleTest {

    @Mock
    IMessageProducer mockedProducer;

    @Test
    public void construct() throws InvalidCronException {
        Schedule s = new Schedule("name0", "command0", "5 10 * * *");
        Assertions.assertEquals("name0", s.getName());
        Assertions.assertEquals("command0", s.getCommand());
        Assertions.assertEquals("5 10 * * *", s.getCronExpression().getStringExpression());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5 30 * * *",
            "5 * * 13 *",
            "5 * 32 * *",
            "* * * * * * *",
    })
    public void construct_throwInvalidCron(String cron) {
        Assertions.assertThrows( InvalidCronException.class , ()->{
            Schedule s = new Schedule("name1", "command1", cron);
        });
    }

    @Test
    public void shouldExecute() throws InvalidCronException{
        Schedule s = new Schedule("name2", "command2", "5 10 * * *");
        Instant testInstant =
                LocalDateTime.of(2019, Month.MARCH, 28, 1, 5, 25).atZone(ZoneId.systemDefault()).toInstant();
        s.clock = Clock.fixed(testInstant, ZoneId.systemDefault());
        Assertions.assertEquals(false, s.shouldExecute());
        testInstant =
                LocalDateTime.of(2019, Month.MARCH, 28, 10, 5, 45).atZone(ZoneId.systemDefault()).toInstant();
        s.clock = Clock.fixed(testInstant, ZoneId.systemDefault());
        Assertions.assertEquals(true, s.shouldExecute());

        s = new Schedule("name3", "command3", "5 10 * * *");
        s.setActive(false);
        Assertions.assertEquals(false, s.shouldExecute());

        s = new Schedule("name4", "command4", "5 10 * * *");
        s.clock = Clock.fixed(testInstant, ZoneId.systemDefault());
        s.setEffectiveTime(LocalDateTime.of(2019, Month.MARCH, 29, 10, 5, 45).atZone(ZoneId.systemDefault()));
        Assertions.assertEquals(true, s.shouldExecute());
        s.setEffectiveTime(LocalDateTime.of(2019, Month.MARCH, 26, 10, 5, 45).atZone(ZoneId.systemDefault()));
        Assertions.assertEquals(false, s.shouldExecute());
    }

    @Test
    public void produceTask() throws Exception {
        Schedule s = new Schedule("name5", "command5", "5 10 * * *");
        s.producer = mockedProducer;
        s.setArgs("a=1 b=2 c=3".split(" "));
        s.produceTask();

        JSONObject json = new JSONObject();
        json.put("a","1");
        json.put("b","2");
        json.put("c","3");
        JSONMessage args = new JSONMessage(json);

        Mockito.verify(s.producer , Mockito.times(1)).send("command5", args);
    }
}
