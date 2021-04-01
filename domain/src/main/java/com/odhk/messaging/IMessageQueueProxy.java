package com.odhk.messaging;

import java.util.Optional;

public interface IMessageQueueProxy {
    boolean createQueue(String name);
    void deleteQueue(String name);

    boolean createExchange(String name, String type);
    void deleteExchange(String name);
}
