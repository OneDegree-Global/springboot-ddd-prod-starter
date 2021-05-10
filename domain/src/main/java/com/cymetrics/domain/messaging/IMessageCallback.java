package com.cymetrics.domain.messaging;

public interface IMessageCallback{
    default void onDelivered(Object message){};
    default Object onCalled(Object arguments){return null;};
    default void onCancel(){};
}