package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;

import static org.mockito.Mockito.spy;
@Testcontainers
public class ConsumerTest {

    MessageConsumerRBMQImp consumer;
    static Channel channel;
    static private GenericContainer rbmq;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queueDelete("test");
        channel.queueDeclare("test",false, false, false, null);
        consumer = spy(new MessageConsumerRBMQImp());
    }

    @BeforeAll
    static public void createChannel() throws Exception {
        rbmq = RBMQTestcontainer.getContainer();

        Integer mappedPort = rbmq.getMappedPort(5672);
        ChannelFactory.port = mappedPort;
        ChannelFactory.userName = "guest";
        ChannelFactory.password = "guest";
        ChannelFactory.host = "127.0.0.1";
        channel = ChannelFactory.getInstance().getChannel();
    }

    @AfterAll
    static public void deleteQueue () throws Exception {
        channel.queueDelete("test");
    }


    @Test
    public void consume_When_MessageInQueue() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] consumeCount = {0};
        Object monitor = new Object();

        consumer.consume("test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                consumeCount[0]++;
                synchronized(monitor) {
                    monitor.notifyAll();
                }
            }
        });

        Assertions.assertEquals(1,channel.consumerCount("test"));
        channel.basicPublish("", "test", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        synchronized (monitor){
            monitor.wait();
        }
        Assertions.assertEquals(1,consumeCount[0]);
    }

    @Test
    public void consumeOnce_When_MessageInQueue() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] consumeCount = {0};
        Object monitor = new Object();

        consumer.consumeOnce("test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                consumeCount[0]++;
                synchronized(monitor) {
                    monitor.notifyAll();
                }
            }
        });

        Assertions.assertEquals(1,channel.consumerCount("test"));
        channel.basicPublish("", "test", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        synchronized (monitor){
            monitor.wait();
        }
        channel.basicPublish("", "test", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        synchronized (monitor){
            monitor.wait(500);
        }
        Assertions.assertEquals(1, consumeCount[0]);
    }

    @Test
    public void multipleConsumer_Executed() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] counter = {0, 0, 0};
        Object monitor = new Object();
        Clock clock = Clock.systemUTC();

        for(int i=0;i<3;i++) {
            final int j = i;
            consumer.consumeOnce("test", new IMessageCallback() {
                @Override
                public void onDelivered(Object message) {
                    counter[j]++;
                }
            });
        }
        for(int i=0;i<3;i++)
            channel.basicPublish("", "test", MessageProperties.PERSISTENT_TEXT_PLAIN, message);

        long startInstant = clock.instant().toEpochMilli();
        while(clock.instant().toEpochMilli() - startInstant < 500){
            if(counter[0]==1 && counter[1]==1 && counter[2] == 1){
                return;
            }
        }
        Assertions.fail("Not all consumer got executed exactly once");
    }

    @Test
    public void removeCallback_NoCallback () throws Exception{
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final boolean[] flag = {false};
        String tag = consumer.consume("test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                flag[0] = true;
            }
        }).get();
        consumer.removeCallback(tag);
        channel.basicPublish("", "test", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        Thread.sleep(300);
        if(flag[0])
            Assertions.fail("Consumer should not get executed");
    }

}
