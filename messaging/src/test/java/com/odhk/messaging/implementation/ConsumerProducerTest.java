package com.odhk.messaging.implementation;

import com.odhk.messaging.Exceptions.ProtocolIOException;
import com.odhk.messaging.Exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageConsumer;
import com.odhk.messaging.IMessageProducer;
import com.odhk.messaging.IMessageQueueProxy;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConsumerProducerTest {

    @Test
    public void queueLifecycle(){
        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.deleteQueue("auth");
            proxy.createQueue("auth");

            proxy.deleteQueue("email");
            proxy.createQueue("email");
            Assertions.assertEquals(2, proxy.getQueueList().size() );
        } catch(QueueLifecycleException e){
            Assertions.fail("Create Queue Fail");
        }
    }

    @Test
    public void basicProduceConsumeOnce()  {
        final boolean[] received = {false, false, false};

        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.createQueue("auth");
            proxy.cleanQueue("auth");

            proxy.createQueue("email");
            proxy.cleanQueue("email");
        } catch(QueueLifecycleException e){
            Assertions.fail("Create Queue Fail");
        }

        Thread consumerThread = new Thread(() -> {
            try {
                IMessageConsumer consumer = new MessageConsumerRBMQImp();

                Optional<String> tag = consumer.consumeOnce("auth", new IMessageCallback() {
                    @Override
                    public void onDelivered(Object message) {
                        Assertions.assertEquals( "TEST AAAA",(String)message);
                        received[0] = !received[0];
                    }
                });

                consumer.consumeOnce("email", new IMessageCallback() {
                    @Override
                    public void onDelivered(Object message) {
                        Assertions.assertEquals( "TEST BBBB",(String)message);
                        received[1] = !received[1];
                    }
                });

                // Not able to reply message anymore
            } catch (ProtocolIOException | QueueLifecycleException e) {
                e.printStackTrace();
            }
        });

        Thread producerThread = new Thread(() -> {
            try {
                IMessageProducer producer = new MessageProducerRBMQImp();

                producer.send("auth", "TEST AAAA");
                producer.send("email", "TEST BBBB");

                Thread.sleep(1000);
                producer.send("auth", "TEST CCCC");

                // Not able to reply message anymore
            } catch (ProtocolIOException | QueueLifecycleException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        consumerThread.start();
        producerThread.start();
        try {
            Thread.sleep(3000);
            Assertions.assertEquals(true,received[0]);
            Assertions.assertEquals(true,received[1]);
        } catch (InterruptedException e){
            Assertions.fail("Interrupted Exception");
        }
    }

    @Test
    public void basicProduceConsume()  {
        final boolean[] received = {false, false, false};

        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();

            proxy.createQueue("auth");
            proxy.cleanQueue("auth");

            proxy.createQueue("email");
            proxy.cleanQueue("email");
        } catch(QueueLifecycleException e){
            Assertions.fail("Create Queue Fail");
        }

        Thread consumerThread = new Thread(() -> {
            try {
                IMessageConsumer consumer = new MessageConsumerRBMQImp();

                Optional<String> tag = consumer.consume("auth", new IMessageCallback() {
                    @Override
                    public void onDelivered(Object message) {
                        Assertions.assertEquals( "TEST AAAA",message);
                        received[0] = !received[0];
                    }
                });

                consumer.consume("email", new IMessageCallback() {
                    @Override
                    public void onDelivered(Object message) {
                        Assertions.assertEquals( "TEST BBBB",message);
                        received[1] = !received[1];
                    }
                });

                // Not able to reply message anymore
            } catch (ProtocolIOException | QueueLifecycleException e) {
                e.printStackTrace();
            }
        });

        Thread producerThread = new Thread(() -> {
            try {
                IMessageProducer producer = new MessageProducerRBMQImp();

                producer.send("auth", "TEST AAAA");
                producer.send("email", "TEST BBBB");

                Thread.sleep(1000);
                producer.send("auth", "TEST AAAA");

                // Not able to reply message anymore
            } catch (ProtocolIOException | QueueLifecycleException| InterruptedException e) {
                e.printStackTrace();
            }
        });

        consumerThread.start();
        producerThread.start();
        try {
            Thread.sleep(3000);
            Assertions.assertEquals(false,received[0]);
            Assertions.assertEquals(true,received[1]);
        } catch (Exception e){
            Assertions.fail("Interrupted Exception");
        }
    }
}
