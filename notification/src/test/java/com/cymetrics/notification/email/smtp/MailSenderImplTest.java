package com.cymetrics.notification.email.smtp;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.mail.*;
import java.util.Properties;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailSenderImplTest {

    static String sender = "test@cymetrics.io";

    @Mock Transport mockTransport;

    private class MockedMailSenderImpl extends MailSenderImpl {
        public MockedMailSenderImpl() throws MessagingException {
        }

        @Override
        // Mock default behavior. We don't really need to connect to anything.
        void connect() {
            SMTPConfig mockConfig = new SMTPConfig();
            mockConfig.setSenderAddress(MailSenderImplTest.sender);
            this.config = mockConfig;

            Properties props = new Properties();
            this.session = Session.getInstance(props);
            this.transport = mockTransport;
        }
    }

    private MockedMailSenderImpl testSender;

    @BeforeEach
    public void setup() throws MessagingException {
        testSender = new MockedMailSenderImpl();
    }

    @Test
    @DisplayName("Mail should be send if data is provided correctly")
    public void send_mail_with_sufficient_info() throws MessagingException, SendTransactionMailFailed {
        EmailSenderPayload payload = new EmailSenderPayload();

        payload.setRecipients(new String[] { "test@test.com" });

        payload.setBcc(new String[] { "bcc@test.com", "bcc2@test.com" });
        payload.setCc(new String[] { "cc@test.com", "cc2@test.com" });

        payload.setSubject("test email");
        payload.setHtmlContent("<html><body><h1>test</h1></body></html>");
        payload.setAlternativeContent("test");
        testSender.send(payload);
        verify(this.mockTransport, times(1)).sendMessage(
            argThat(message -> {
                try {
                    Assertions.assertEquals(message.getSubject(), payload.getSubject());
                    Assertions.assertEquals(message.getFrom()[0].toString(), MailSenderImplTest.sender);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }),
            argThat(recipients -> {
                Assertions.assertEquals(recipients.length, 5);
                return true;
            })
        );
    }

    @Test
    @DisplayName("Reconnect should NOT be triggered if we're connecting")
    public void should_not_trigger_reconnect() throws SendTransactionMailFailed, MessagingException {
        EmailSenderPayload payload = new EmailSenderPayload();
        when(this.mockTransport.isConnected()).thenReturn(true);
        testSender.send(payload);
        verify(this.mockTransport, times(0)).connect();
        verify(this.mockTransport, times(1)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("Reconnect should be triggered if we're not connecting")
    public void should_trigger_reconnect() throws SendTransactionMailFailed, MessagingException {
        EmailSenderPayload payload = new EmailSenderPayload();
        when(this.mockTransport.isConnected()).thenReturn(false);
        testSender.send(payload);

        verify(this.mockTransport, times(1)).connect();
        verify(this.mockTransport, times(1)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("[SendTransactionMailFailed] should be thrown if reconnection failed")
    public void reconnect_failed_should_be_handled() throws MessagingException {
        EmailSenderPayload payload = new EmailSenderPayload();
        doThrow(MessagingException.class).when(this.mockTransport).connect();
        Assertions.assertThrows(SendTransactionMailFailed.class, () -> testSender.send(payload));
    }


    @Test
    @DisplayName("[SendTransactionMailFailed] should be thrown if send failed")
    public void send_failed_should_be_handled() throws MessagingException {
        EmailSenderPayload payload = new EmailSenderPayload();
        doThrow(MessagingException.class).when(this.mockTransport).sendMessage(any(), any());
        Assertions.assertThrows(SendTransactionMailFailed.class, () -> testSender.send(payload));
    }
}
