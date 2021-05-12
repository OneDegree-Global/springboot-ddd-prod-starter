package com.cymetrics.domain.transactionmail.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.interfaces.MailSender;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmailSenderTest {

    @Mock MailSender mockSender;
    ListAppender<ILoggingEvent> appender;
    Logger logger = (Logger) LoggerFactory.getLogger(EmailSender.class);

    EmailSender emailSender;

    @BeforeEach
    public void setup() {
        this.emailSender = new EmailSender();
        this.emailSender.HIGH_LEVEL_INTERVAL = 0;
        this.emailSender.sender = mockSender;
        this.appender = new ListAppender<>();
        this.logger.addAppender(this.appender);
        this.appender.start();
    }

    @AfterEach
    public void cleanup() {
        this.appender.stop();
        this.logger.detachAppender(appender);
    }

    @Test
    @DisplayName("Make sure that low level mail is sent only once before termination")
    public void low_level_mail_send_failed() throws SendTransactionMailFailed {

        doThrow(SendTransactionMailFailed.class).when(this.mockSender).send(any());

        EmailSenderPayload payload = new EmailSenderPayload();
        emailSender.sendLowLevelMail(payload);

        verify(this.mockSender, times(1)).send(any());
        List<ILoggingEvent> logs = appender.list;
        Assertions.assertEquals(1, logs.size());

        ILoggingEvent log = logs.get(0);
        Assertions.assertEquals(Level.ERROR, log.getLevel());
        Assertions.assertEquals("SEND_FAIL_LOW", log.getMarker().toString());
    }


    @Test
    @DisplayName("Sending HIGH level mail should attempts 5 times before logging and termination")
    public void high_level_mail_send_failed() throws SendTransactionMailFailed {

        doThrow(SendTransactionMailFailed.class).when(this.mockSender).send(any());

        EmailSenderPayload payload = new EmailSenderPayload();
        emailSender.sendHighLevelMail(payload);

        verify(this.mockSender, times(3)).send(any());

        List<ILoggingEvent> logs = appender.list;
        Assertions.assertEquals(1, logs.size());

        ILoggingEvent log = logs.get(0);
        Assertions.assertEquals(Level.ERROR, log.getLevel());
        Assertions.assertEquals("SEND_FAIL_HIGH", log.getMarker().toString());
    }

    @Test
    @DisplayName("If mail sending functionality resumes after retry, there should be no error log")
    public void high_mail_send_success_after_retry() throws SendTransactionMailFailed {

        doThrow(SendTransactionMailFailed.class)
            .doNothing()
            .when(this.mockSender).send(any());

        EmailSenderPayload payload = new EmailSenderPayload();
        emailSender.sendHighLevelMail(payload);

        verify(this.mockSender, times(2)).send(any());

        List<ILoggingEvent> logs = appender.list;
        Assertions.assertEquals(0, logs.size());

    }
}
