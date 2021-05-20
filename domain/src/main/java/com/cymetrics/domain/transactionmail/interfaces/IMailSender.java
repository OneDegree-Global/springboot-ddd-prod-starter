package com.cymetrics.domain.transactionmail.interfaces;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;

public interface IMailSender {

    // TODO: file attachment
    public void send(EmailSenderPayload payload) throws SendTransactionMailFailed;

}

