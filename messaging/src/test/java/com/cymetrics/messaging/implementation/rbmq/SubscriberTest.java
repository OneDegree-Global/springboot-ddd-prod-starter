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
public class SubscriberTest {

    static Channel channel;
    MessageSubscriberRBMQImp subscriber;
    static private GenericContainer rbmq;


    @BeforeEach
    public void cleanQueue() throws Exception {
        channel.queueDelete("test");
        channel.queueDelete("test2");
        channel.queueDeclare("test",false,false,false, null);
        channel.queueDeclare("test2",false,false,false, null);
        channel.queueBind("test", "test_exchange", "");
        channel.queueBind("test2", "test_exchange", "");

        subscriber = spy(new MessageSubscriberRBMQImp());
    }

    @BeforeAll
    static public void createChannel() throws Exception{
        rbmq = RBMQTestcontainer.getContainer();
        Integer mappedPort = rbmq.getMappedPort(5672);
        ChannelFactory.port = mappedPort;
        ChannelFactory.userName = "guest";
        ChannelFactory.password = "guest";
        ChannelFactory.host = "127.0.0.1";
        channel = ChannelFactory.getInstance().getChannel();
        channel.exchangeDeclare("test_exchange", "fanout");
    }

    @AfterAll
    static public void deleteQueue () throws Exception {
        channel.exchangeDelete("test_exchange");
        channel.queueUnbind("test", "test_exchange", "");
        channel.queueUnbind("test2", "test_exchange", "");
    }

    @Test
    public void subscribe_And_Consume() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] counter = {0,0};
        Clock clock = Clock.systemUTC();

        subscriber.subscribe("test_exchange","test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                counter[0]++;
            }
        });
        subscriber.subscribe("test_exchange","test2", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                counter[1]++;
            }
        });

        channel.basicPublish("test_exchange", "", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        long startInstant = clock.instant().toEpochMilli();
        while(clock.instant().toEpochMilli() - startInstant < 500){
            if(counter[0]==1 && counter[1]==1){
                return;
            }
        }
        Assertions.fail("Not all consumer got executed exactly once");
    }


    @Test
    public void subscribe_MultipleConsume() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] counter = {0, 0};
        Clock clock = Clock.systemUTC();

        subscriber.subscribe("test_exchange","test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                counter[0]++;
            }
        });
        subscriber.subscribe("test_exchange","test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                counter[1]++;
            }
        });

        channel.basicPublish("test_exchange", "", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        channel.basicPublish("test_exchange", "", MessageProperties.PERSISTENT_TEXT_PLAIN, message);

        long startInstant = clock.instant().toEpochMilli();
        while(clock.instant().toEpochMilli() - startInstant < 500){
            if(counter[0]==1 && counter[1]==1){
                return;
            }
        }
        Assertions.fail("Not all consumer got executed exactly once");
    }

    @Test
    public void subscribe_Unsubscribe() throws Exception{
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final boolean[] flag = {false};
        String tag = subscriber.subscribe("test_exchange", "test", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
               flag[0] = true;
            }
        }).get();

        subscriber.unsubscribe("test_exchange", "test");

        channel.basicPublish("test_exchange", "", MessageProperties.PERSISTENT_TEXT_PLAIN, message);

        Thread.sleep(300);
        if(flag[0])
            Assertions.fail("Consumer should not get executed");
    }


}
