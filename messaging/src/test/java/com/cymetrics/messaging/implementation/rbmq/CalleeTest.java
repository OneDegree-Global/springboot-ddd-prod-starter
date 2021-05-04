package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.messaging.IMessageCallback;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.util.UUID;

import static org.mockito.Mockito.spy;

@Testcontainers
@Tag("slow")
public class CalleeTest {

    MessageCalleeRBMQImp callee;
    static Channel channel;

    static private GenericContainer rbmq;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queueDelete("test");
        channel.queueDeclare("test",false, false, false, null);
        callee = spy(new MessageCalleeRBMQImp());
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
    public void calleeCalled_ReplyMessage() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] counter = {0};
        Clock clock = Clock.systemUTC();
        callee.consumeAndReply("test", new IMessageCallback() {
            @Override
            public Object onCalled(Object arguments) {
                counter[0]++;
                return "reply";
            }
        });

        String replyQueueName = channel.queueDeclare().getQueue();
        final String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        channel.basicPublish("", "test", props, message);
        long limit = clock.instant().toEpochMilli() + 1000;
        GetResponse response;
        while((response=channel.basicGet(replyQueueName,false))==null &&
                clock.instant().toEpochMilli()<limit);
        Assertions.assertEquals(1,counter[0]);
        if(response==null)
            Assertions.fail("Callee did not reply message correctly");
        Assertions.assertEquals("reply",(String)ObjectByteConverter.decodeObject(response.getBody()));
    }

    @Test
    public void multipleCalleeFunction_Executed() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        final int[] counter = {0, 0, 0};
        Clock clock = Clock.systemUTC();

        for(int i=0;i<3;i++) {
            final int j = i;
            callee.consumeAndReply("test", new IMessageCallback() {
                @Override
                public Object onCalled(Object message) {
                    counter[j]++;
                    return "reply"+j;
                }
            });
        }

        for(int k=0;k<3;k++) {
            String replyQueueName = channel.queueDeclare().getQueue();
            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            channel.basicPublish("", "test", props , message);
        }

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
        String tag = callee.consumeAndReply("test", new IMessageCallback() {
            @Override
            public Object onCalled(Object message) {
                flag[0] = true;
                return "anything";
            }
        }).get();
        callee.removeCallback(tag);

        String replyQueueName = channel.queueDeclare().getQueue();
        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        channel.basicPublish("", "test", props , message);
        Thread.sleep(300);
        if(flag[0])
            Assertions.fail("Consumer should not get executed");
    }

}
