package com.cymetrics.domain.transactionmail.services.common;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.interfaces.IMailSender;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.inject.Inject;

public class EmailSender {

    @Inject
    IMailSender sender;

     int HIGH_LEVEL_INTERVAL = 50;
     int HIGH_LEVEL_RETRY_COUNT = 3;

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private Marker highLevelMarker = MarkerFactory.getMarker("SEND_FAIL_HIGH");
    private Marker lowLevelMarker = MarkerFactory.getMarker("SEND_FAIL_LOW");


    private void sendWithAttempts(int attempts, int interval, EmailSenderPayload payload) throws SendTransactionMailFailed {

        if (!(attempts > 0)) throw new SendTransactionMailFailed("Invalid attempt count");

        int iterateCount = 0;
        while (iterateCount < attempts) {
            try {
                this.sender.send(payload);
                return;
            } catch (SendTransactionMailFailed e) {
                iterateCount++;
            }

            try {
                Thread.interrupted();
                Thread.sleep((long) Math.pow(interval, iterateCount));
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new SendTransactionMailFailed(
                        String.format(
                                "Mail sending process terminated. Attempt limit: %d, Current attempt: %d, Error: %s",
                                attempts,
                                iterateCount,
                                e.getMessage()
                        )
                );
            }
        }

        throw new SendTransactionMailFailed(String.format("Unable to send email after %d attempts", iterateCount));
    }

    // Mail with HIGH level should be retried multiple times with shorter period
    public void sendHighLevelMail(EmailSenderPayload payload) {
        try {
            this.sendWithAttempts(this.HIGH_LEVEL_RETRY_COUNT, this.HIGH_LEVEL_INTERVAL, payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(highLevelMarker, e.getMessage(), payload);
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
