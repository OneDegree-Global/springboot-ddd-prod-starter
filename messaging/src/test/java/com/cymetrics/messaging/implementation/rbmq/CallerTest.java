package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.cymetrics.messaging.messageTypes.JSONMessage;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Testcontainers
@Tag("slow")
public class CallerTest {

    MessageCallerRBMQImp caller;
    static Channel channel;

    static private GenericContainer rbmq;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queuePurge("test");
        caller = spy(new MessageCallerRBMQImp());
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
        channel.queueDeclare("test",false,false,false, null);
    }

    @AfterAll
    static public void deleteQueue () throws Exception {
        channel.queueDelete("test");
    }

    @Test
    public void callWithByteMessage() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        String[] failMessage = {null};
        Thread t = new Thread(()-> {
            try {
                Thread.sleep(300);
                if(channel.messageCount("test")!=1)
                    failMessage[0] = "Message count incorrect";

                GetResponse response = channel.basicGet("test", false);
                String corrId = response.getProps().getCorrelationId();
                String replyTo = response.getProps().getReplyTo();

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(corrId)
                        .build();

                channel.basicPublish("", replyTo, replyProps, ObjectByteConverter.encodeObject("reply"));
            } catch(Exception e){
                failMessage[0] = "exception happen in mock callee:"+e;
            }
        });
        t.start();
        Optional<Object> o = caller.sendAndGetReply("test", message, 1000);

        if(failMessage[0]!=null)
            Assertions.fail(failMessage[0]);
        if(o.isEmpty())
            Assertions.fail("Get reply failed");
        Assertions.assertEquals("reply", (String)o.get());
    }

    @Test
    public void callerCalled_Timeout() throws Exception {
        String[] failMessage = {null};
        caller.clock = Clock.fixed(Instant.ofEpochSecond(1617171335), ZoneId.of("UTC"));
        Thread t = new Thread(()->{
            try {
                Optional<Object> o = caller.sendAndGetReply("test","whatever",10000);
                if(o.isPresent())
                    failMessage[0] = "when timeout reply should be null";
            } catch (ProtocolIOException e) {
                failMessage[0] = e.toString();
            }
        });
        t.start();
        Thread.sleep(100);
        caller.clock = Clock.fixed(Instant.ofEpochSecond(1617171355), ZoneId.of("UTC"));
        t.join();
        if(failMessage[0]!=null)
            Assertions.fail(failMessage[0]);
    }

    @Test
    public void callWithStringMessage() throws Exception {
        caller.sendAndGetReply("test","whatever",100);
        verify(caller).sendAndGetReply("test", ObjectByteConverter.encodeObject("whatever"),100);
    }


    @Test
    public void callWithJSONMessage() throws Exception {

        HashMap<String,String> hm = new HashMap<>();
        hm.put("key1","value1");
        hm.put("key2","value2");
        JSONObject json = new JSONObject(hm);
        JSONMessage message = new JSONMessage(json);

        caller.sendAndGetReply("test",message,100);
        verify(caller).sendAndGetReply("test", ObjectByteConverter.encodeObject(message),100);
    }

    @Test
    public void produceWrongTypeMessage_ThrowException() throws ProtocolIOException {
        assertThrows( ProtocolIOException.class , () -> {
            HashMap<String,String> hm = new HashMap<>();
            hm.put("key1","value1");
            hm.put("key2","value2");
            JSONObject json = new JSONObject();

            caller.sendAndGetReply("test", json,100);
        });
    }
}
