package com.odhk.messaging;

public interface IMessageCallback{
    void onDelivered(Object message);
    default Object onCalled(Object message){return null;};
    default void onCancel(){};
}