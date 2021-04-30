package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.messaging.IMessageCallback;
import com.cymetrics.messaging.IMessageConsumer;
import com.cymetrics.messaging.IMessageProducer;
import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.exceptions.QueueLifecycleException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@ExtendWith(MockitoExtension.class)
public class ConsumerProducerTest {

    static MessageProxyRBMQImp proxy;
    static private GenericContainer rbmq;

    @BeforeEach
    public void cleanQueue() throws QueueLifecycleException {
        proxy.cleanQueue("auth");
        proxy.cleanQueue("email");
    }

    @BeforeAll
    static public void createQueue() throws Exception{
        rbmq = RBMQTestcontainer.getContainer();
        Integer mappedPort = rbmq.getMappedPort(5672);
        ChannelFactory.port = mappedPort;
        ChannelFactory.userName = "guest";
        ChannelFactory.password = "guest";
        ChannelFactory.host = "127.0.0.1";

        proxy = new MessageProxyRBMQImp();
        proxy.createQueue("auth");
        proxy.createQueue("email");
    }

    @AfterAll
    static public void deleteQueue() throws Exception{
        proxy.deleteQueue("auth");
        proxy.deleteQueue("email");
    }

    @Test
    public void basicProduceConsumeOnce(){
        final boolean[] received = {false, false, false};

        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.cleanQueue("auth");
            proxy.cleanQueue("email");
        } catch(QueueLifecycleException e){
            Assertions.fail("Clean queue Fail");
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
                Thread.currentThread().interrupt();
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
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void basicProduceConsume()  {
        final boolean[] received = {false, false, false};
        String failMessage = null;

        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.cleanQueue("auth");
            proxy.cleanQueue("email");
        } catch(QueueLifecycleException e){
            Assertions.fail("Clean Queue Fail");
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
                Thread.currentThread().interrupt();
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
