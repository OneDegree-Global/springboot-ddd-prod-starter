package com.odhk.messaging;

import com.odhk.messaging.exceptions.QueueLifecycleException;

public interface IMessageQueueProxy {
    // raise request to RBMQ server to create/clean/delete queue
    void createQueue(String name) throws QueueLifecycleException;
    void deleteQueue(String name) throws QueueLifecycleException;
    void cleanQueue(String name) throws QueueLifecycleException;

    void createTopic(String name) throws QueueLifecycleException;
    void deleteTopic(String name) throws QueueLifecycleException;

}
