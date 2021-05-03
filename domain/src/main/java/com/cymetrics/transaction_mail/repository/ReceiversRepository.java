package com.cymetrics.transaction_mail.repository;

import java.util.Optional;

import com.cymetrics.transaction_mail.model.Receiver;

public interface ReceiversRepository {

    public Optional<Receiver> getReceiverByAddress(String address);
    public Optional<Receiver> getReceiverById(String Id);
    public void save(Receiver receiver);
    public void delete(Receiver Receiver);

    // public Optional<Receiver[]> getReceiversByGroup(String address);
}
