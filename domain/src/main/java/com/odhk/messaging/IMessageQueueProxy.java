package com.odhk.messaging;

import java.util.Optional;

public interface IMessageQueueProxy {
    boolean createQueue(String name);
    void deleteQueue(String name);

    boolean createTopic(String name);
    void deleteTopic(String name);
}
