package com.cymetrics.domain.transactionmail.utils;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.interfaces.MailSender;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.inject.Inject;

public class EmailSender {

    @Inject MailSender sender;
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private Marker highLevelMarker = MarkerFactory.getMarker("SEND_FAIL_HIGH");
    private Marker mediumLevelMarker = MarkerFactory.getMarker("SEND_FAIL_MEDIUM");
    private Marker lowLevelMarker = MarkerFactory.getMarker("SEND_FAIL_LOW");


    public void sendWithAttempts(int attempts, int interval, EmailSenderPayload payload) throws SendTransactionMailFailed {

        if (!(attempts > 0)) throw new SendTransactionMailFailed("Invalid attempt count");
        if (!(interval > 0)) throw new SendTransactionMailFailed("Invalid interval amount");

        int iterateCount = 0;
        while (iterateCount < attempts) {
            try {
                this.sender.send(payload);
                return;
            } catch (SendTransactionMailFailed e) {
                try {
                    Thread.interrupted();
                    Thread.sleep(interval);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new SendTransactionMailFailed(

                        String.format(
                            "Mail sending process is terminated due to interruption. Attempt limit: %d, Current attempt: %d, Error: %s",
                            attempts,
                            iterateCount,
                            interruptedException.getMessage()
                        )
                    );
                }
            }
            iterateCount++;
        }

        throw new SendTransactionMailFailed(String.format("Unable to send email after %d attempts", iterateCount));
    }

    // Mail with HIGH level should be retried multiple times with shorter period
    public void sendHighLevelMail(EmailSenderPayload payload) {
        try {
            this.sendWithAttempts(5, 10, payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(highLevelMarker, e.getMessage(), payload);
        }
    }

    // Mail with MEDIUM level only retry once with longer period
    public void sendMediumLevelMail(EmailSenderPayload payload) {
        try {
            this.sendWithAttempts(2, 10, payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(mediumLevelMarker, e.getMessage(), payload);
        }
    }

    // LOW level mail does not require retry. It can be handled manually.
    public void sendLowLevelMail(EmailSenderPayload payload) {
        try {
            this.sender.send(payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(lowLevelMarker, e.getMessage(), payload);
        }
    }

}
