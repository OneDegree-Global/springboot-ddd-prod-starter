package com.odhk.messaging;

public interface IMessageCallback{
    void onDelivered(Object message);
    default void onCancel(){};
}