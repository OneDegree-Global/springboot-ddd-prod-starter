package com.cymetrics.transaction_mail.interfaces;

import com.cymetrics.transaction_mail.exceptions.SendTransactionMailFailed;

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

