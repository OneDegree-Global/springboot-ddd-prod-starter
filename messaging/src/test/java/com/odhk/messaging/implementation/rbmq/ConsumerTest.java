package com.odhk.messaging.implementation.rbmq;

import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageConsumer;
import com.odhk.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import org.junit.jupiter.api.*;
import org.mockito.Spy;

import java.time.Clock;

import static org.mockito.Mockito.spy;

public class ConsumerTest {

    MessageConsumerRBMQImp consumer;
    static Channel channel;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queueDelete("test");
        channel.queueDeclare("test",false, false, false, null);
        consumer = spy(new MessageConsumerRBMQImp());
    }

    @BeforeAll
    static public void createQueue() throws Exception {
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
