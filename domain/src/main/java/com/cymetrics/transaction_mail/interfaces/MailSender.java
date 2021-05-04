package com.cymetrics.transaction_mail.interfaces;

import com.cymetrics.transaction_mail.exceptions.SendTransactionMailFailed;

public interface MailSender {

    public void send(
        String[] recipients,
        String[] cc,
        String[] bcc,
        String subject,
        String htmlContent,
        String alternativeContent
        // DataSource[] attachment,
    ) throws SendTransactionMailFailed;

}

