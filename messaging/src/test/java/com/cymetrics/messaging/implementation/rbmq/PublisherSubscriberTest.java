package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessagePublisher;
import com.cymetrics.domain.messaging.IMessageSubscriber;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
public class PublisherSubscriberTest {

    static MessageProxyRBMQImp proxy;
    static private GenericContainer rbmq;


    @BeforeEach
    public void cleanQueue() throws QueueLifecycleException {
        for (int i = 1; i <= 3; i++) {
            proxy.cleanQueue("userAuthedListener"+i);
            proxy.cleanQueue("mailSentListener"+i);
        }
    }

    @BeforeAll
    static public void createQueue() throws Exception{
        rbmq = RBMQTestcontainer.getContainer();

        Integer mappedPort = rbmq.getMappedPort(5672);
        RBMQConfig config = new RBMQConfig("guest", "guest", "127.0.0.1", mappedPort);
        ChannelFactory.config = config;
        proxy = new MessageProxyRBMQImp();
        proxy.createTopic("userAuthed");
        proxy.createTopic("mailSent");
        for(int i=1; i<=3;i++) {
            proxy.createQueue("userAuthedListener"+i);
            proxy.createQueue("mailSentListener"+i);
        }
    }

    @AfterAll
    static public void deleteQueue() throws Exception{
        for(int i=1; i<=3;i++) {
            proxy.deleteQueue("userAuthedListener"+i);
            proxy.deleteQueue("mailSentListener"+i);
        }
    }


    @Test
    public void basicPublishSubscribe() throws InterruptedException {

            final int[] receiveCount = {0,0};


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


    }
}