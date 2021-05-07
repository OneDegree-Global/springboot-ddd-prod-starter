package com.cymetrics.domain.transactionmail.repository;

import java.util.Optional;

import com.cymetrics.domain.transactionmail.aggregates.Receiver;

public interface ReceiverRepository {

    public Optional<Receiver> getReceiverByEmail(String email);

}
