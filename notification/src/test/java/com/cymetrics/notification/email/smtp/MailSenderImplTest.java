package com.cymetrics.notification.email.smtp;


import ch.qos.logback.core.read.ListAppender;
import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import com.cymetrics.domain.transactionmail.services.common.EmailSender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

@ExtendWith(MockitoExtension.class)
public class MailSenderImplTest {

    @BeforeEach
    public void setup() {

    }

    @Test
    @DisplayName("test hahaha")
    public void test() throws MessagingException, SendTransactionMailFailed, InterruptedException {
        MailSenderImpl sender = new MailSenderImpl();
        Thread t1 = new Thread() {
            @SneakyThrows
            public void run() {
                sender.testSend();
            }
        };

        Thread t2 = new Thread() {
            @SneakyThrows
            public void run() {
                sender.testSend();
            }
        };

        t1.start();
        t2.start();

        Thread.sleep(20000);

    }

}
