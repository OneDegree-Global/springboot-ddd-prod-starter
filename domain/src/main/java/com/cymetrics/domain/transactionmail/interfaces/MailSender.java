package com.cymetrics.domain.transactionmail.interfaces;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;

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

