package com.cymetrics.transaction_mail.repository;

import java.util.Optional;

import com.cymetrics.transaction_mail.model.Receiver;

public interface ReceiverRepository {

    public Optional<Receiver> getReceiverByEmail(String email);

}
