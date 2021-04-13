package com.odhk.messaging.implementation;

import com.odhk.messaging.Exceptions.ProtocolIOException;
import com.odhk.messaging.Exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessagePublisher;
import com.odhk.messaging.IMessageQueueProxy;
import com.odhk.messaging.IMessageSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class PublisherSubscriberTest {

    @Test
    public void exchangeLifecycle() {
        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.createTopic("userAuthed");
            proxy.createTopic("mailSent");

            proxy.createQueue("userAuthedListener");
            proxy.createQueue("mailSentListener");

            Assertions.assertEquals(2, proxy.getExchangeList().size());
        } catch (QueueLifecycleException e) {
            Assertions.fail("Create exchange Fail");
        }
    }

    @Test
    public void basicPublishSubscribe() {
        try {
            final int[] receiveCount = {0,0};
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.createTopic("userAuthed");

            for (int i = 1; i <= 3; i++) {
                proxy.createQueue("userAuthedListener" + i);
                proxy.cleanQueue("userAuthedListener"+i);

                proxy.createQueue("mailSentListener" + i);
                proxy.cleanQueue("mailSentListener"+i);

            }


            Thread subscriberThread = new Thread( new Runnable(){
                @Override
                public void run(){
                try {
                    IMessageSubscriber subscriber = new MessageSubscriberRBMQImp();
                    Optional<String>[] tags = new Optional[3];

                    for (int i = 1; i <= 3; i++) {
                        final int j = i;
                        tags[j-1] = subscriber.subscribe("userAuthed", "userAuthedListener" + j, new IMessageCallback() {
                            String queueName = "userAuthedListener" + j;
                            @Override
                            public void onDelivered(Object message) {
                                synchronized (receiveCount){
                                    receiveCount[0]++;
                                }
                                Assertions.assertEquals("TEST AAAA",(String)message);
                            }
                        });
                    }

                    for(int i=1;i<=3;i++){
                        final int j = i;
                        subscriber.subscribe("mailSent","mailSentListener"+j, new IMessageCallback() {

                            String queueName = "mailSentListener"+j;
                            @Override
                            public void onDelivered(Object message) {
                                synchronized (receiveCount){
                                    receiveCount[1]++;
                                }
                                Assertions.assertEquals("TEST BBBB",(String)message);
                            }
                        });
                    }

                    Thread.sleep(3000);

                    subscriber.unsubscribe("userAuthed", "userAuthedListener1");
                    subscriber.unsubscribe("userAuthed", "userAuthedListener3");


                    subscriber.removeCallback(tags[0].orElse(""));
                    subscriber.removeCallback(tags[2].orElse(""));


                    // Not able to reply message anymore
                } catch (ProtocolIOException | QueueLifecycleException | InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }});

            Thread publisherThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {

                        IMessagePublisher publisher = new MessagePublisherRBMQImp();

                        Thread.sleep(2000);

                        publisher.publish("userAuthed", "TEST AAAA");
                        publisher.publish("mailSent", "TEST BBBB");

                        Thread.sleep(3000);

                        publisher.publish("userAuthed", "TEST AAAA");
                        // Not able to reply message anymore
                    } catch (QueueLifecycleException | ProtocolIOException | InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });

            publisherThread.start();
            subscriberThread.start();

            Thread.sleep(6000);
            Assertions.assertEquals(4,receiveCount[0]);
            Assertions.assertEquals(3,receiveCount[1]);

        } catch(QueueLifecycleException | InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}