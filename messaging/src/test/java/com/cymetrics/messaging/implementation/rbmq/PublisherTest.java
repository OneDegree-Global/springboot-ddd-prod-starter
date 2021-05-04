package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.messaging.exceptions.ProtocolIOException;
import com.cymetrics.messaging.implementation.utils.ObjectByteConverter;
import com.cymetrics.messaging.messageTypes.JSONMessage;
import com.rabbitmq.client.Channel;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Testcontainers
@Tag("slow")
public class PublisherTest {


    MessagePublisherRBMQImp publisher;
    static Channel channel;
    static private GenericContainer rbmq;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queueDelete("test");
        channel.queueDelete("test2");
        channel.queueDeclare("test",false, false, false, null);
        channel.queueDeclare("test2",false, false, false, null);
        channel.queueBind("test", "test_exchange", "");
        channel.queueBind("test2", "test_exchange", "");
        publisher = spy(new MessagePublisherRBMQImp());
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
        channel.exchangeDeclare("test_exchange", "fanout");
    }

    @AfterAll
    static public void deleteQueue () throws Exception {
        channel.queueDelete("test");
        channel.queueDelete("test2");
        channel.exchangeDelete("test_exchange");
        channel.queueUnbind("test", "test_exchange", "");
        channel.queueUnbind("test2", "test_exchange", "");
    }

    @Test
    public void publishByteMessage() throws Exception {
        byte[] message = ObjectByteConverter.encodeObject("whatever");
        publisher.publish("test_exchange", message);

        byte[] message2 = ObjectByteConverter.encodeObject("whatever2");
        publisher.publish("test_exchange", message2);


        Thread.sleep(1000);
        Assertions.assertEquals(2, channel.messageCount("test"));
        Assertions.assertEquals(2, channel.messageCount("test2"));

        String s1 = (String) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());
        String s2 = (String) ObjectByteConverter.decodeObject(channel.basicGet("test2",false).getBody());
        Assertions.assertEquals("whatever", s1);
        Assertions.assertEquals("whatever", s2);

        Assertions.assertEquals(1, channel.messageCount("test"));
        Assertions.assertEquals(1, channel.messageCount("test2"));
    }

    @Test
    public void publishStringMessage() throws Exception {
        publisher.publish("test_exchange","whatever");
        publisher.publish("test_exchange","whatever2");

        verify(publisher).publish("test_exchange", ObjectByteConverter.encodeObject("whatever"));
        verify(publisher).publish("test_exchange", ObjectByteConverter.encodeObject("whatever2"));


        String s1 = (String) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());
        String s2 = (String) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());
        Assertions.assertEquals("whatever",s1);
        Assertions.assertEquals("whatever2",s2);
    }

    @Test
    public void publishJSONMessage() throws Exception {
        HashMap<String,String> hm = new HashMap<>();
        hm.put("key1","value1");
        hm.put("key2","value2");
        JSONObject json = new JSONObject(hm);
        JSONMessage message = new JSONMessage(json);

        publisher.publish("test_exchange", message);
        verify(publisher).publish("test_exchange", ObjectByteConverter.encodeObject(message));

        JSONMessage response = (JSONMessage) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());

        Assertions.assertEquals("value1", response.getJSON().get("key1"));
        Assertions.assertEquals("value2", response.getJSON().get("key2"));

    }

    @Test
    public void publishWrongTypeMessage_ThrowException() throws ProtocolIOException {
        assertThrows( ProtocolIOException.class , () -> {
            HashMap<String,String> hm = new HashMap<>();
            hm.put("key1","value1");
            hm.put("key2","value2");
            JSONObject json = new JSONObject();

            publisher.publish("test", json);
        });
    }
}
