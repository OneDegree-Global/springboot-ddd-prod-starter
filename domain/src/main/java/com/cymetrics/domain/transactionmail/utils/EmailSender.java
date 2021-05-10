package com.cymetrics.domain.transactionmail.utils;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.interfaces.MailSender;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;
import com.cymetrics.domain.transactionmail.services.TransactionEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.inject.Inject;

public class EmailSender {

    @Inject MailSender sender;
    private static Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public enum levelOfImportance {
        HIGH,
        MEDIUM,
        LOW
    }

    private Marker highLevelMarker = MarkerFactory.getMarker("SEND_FAIL_HIGH");
    private Marker mediumLevelMarker = MarkerFactory.getMarker("SEND_FAIL_MEDIUM");
    private Marker lowLevelMarker = MarkerFactory.getMarker("SEND_FAIL_LOW");


    private void sendWithRetry(int retries, int interval, EmailSenderPayload payload) throws SendTransactionMailFailed {

        if (!(retries > 0)) throw new SendTransactionMailFailed("Invalid retry count");
        if (!(interval > 0)) throw new SendTransactionMailFailed("Invalid interval amount");

        int iterateCount = 0;
        while (iterateCount < retries) {
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
                            "Retry is interrupted. Retry Limit: %d, Current Retry: %d, Error: %s",
                            retries,
                            iterateCount,
                            interruptedException.getMessage()
                        )
                    );
                }
            }
            iterateCount++;
        }

        throw new SendTransactionMailFailed(String.format("Unable to send email after %d retries", iterateCount));
    }



    /*
        Mail with HIGH level should be retried multiple times with shorter period
     */
    private void sendHighLevelMail(EmailSenderPayload payload) {
        try {
            this.sendWithRetry(5, 1000, payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(highLevelMarker, e.getMessage(), payload);
        }
    }

    /*
        Mail with MEDIUM level only retry once with longer period
     */
    private void sendMediumLevelMail(EmailSenderPayload payload) {
        try {
            this.sendWithRetry(1, 10000, payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(mediumLevelMarker, e.getMessage(), payload);
        }
    }

    /*
        LOW level mail does not require retry. It can be handled manually.
     */
    private void sendLowLevelMail(EmailSenderPayload payload) {
        try {
            this.sender.send(payload);
        } catch (SendTransactionMailFailed e) {
            logger.error(lowLevelMarker, e.getMessage(), payload);
        }
    }

    // TODO: failure cases handling based on priority
    public void sendWithImportanceLevel(levelOfImportance importanceLevel, EmailSenderPayload payload) {
        switch (importanceLevel) {
            case HIGH:
                this.sendHighLevelMail(payload);
                return;
            case MEDIUM:
                this.sendMediumLevelMail(payload);
                return;
            case LOW:
                this.sendLowLevelMail(payload);
                return;
        }
    }

}
