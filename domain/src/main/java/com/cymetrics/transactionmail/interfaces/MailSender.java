package com.cymetrics.transactionmail.interfaces;

import com.cymetrics.transactionmail.exceptions.SendTransactionMailFailed;

public interface MailSender {

    // TODO: file attachment
    public void send(
        String[] recipients,
        String[] cc,
        String[] bcc,
        String subject,
        String htmlContent,
        String alternativeContent
    ) throws SendTransactionMailFailed;

}

