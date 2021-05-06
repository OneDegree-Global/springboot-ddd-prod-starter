package com.cymetrics.transactionmail.repository;

import java.util.Optional;

import com.cymetrics.transactionmail.aggregates.Receiver;

public interface ReceiverRepository {

    public Optional<Receiver> getReceiverByEmail(String email);

}
