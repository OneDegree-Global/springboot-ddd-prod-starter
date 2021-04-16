package com.odhk.messaging.implementation.rbmq;

import com.odhk.messaging.IMessageProducer;
import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.implementation.utils.ObjectByteConverter;
import com.odhk.messaging.messageTypes.JSONMessage;
import com.rabbitmq.client.Channel;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Testcontainers
public class ProducerTest {

    IMessageProducer producer;
    static  Channel channel;

    @Container
    static public GenericContainer rbmq = new GenericContainer(DockerImageName.parse("rabbitmq:5.11"))
            .withExposedPorts(25672);

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queuePurge("test");
        producer = spy(new MessageProducerRBMQImp());
    }

    @BeforeAll
    static public void createQueue() throws Exception {
        ChannelFactory.port = 25672;
        ChannelFactory.userName = "admin";
        ChannelFactory.password = "admin";
        ChannelFactory.host = "127.0.0.1";

        channel = ChannelFactory.getInstance().getChannel();
        channel.queueDeclare("test",false,false,false, null);
    }

    @AfterAll
    static public void deleteQueue () throws Exception {
        channel.queueDelete("test");
    }


    @Test
    public void produceByteMessage() throws Exception {

        byte[] message = ObjectByteConverter.encodeObject("whatever");
        producer.send("test", message);

        byte[] message2 = ObjectByteConverter.encodeObject("whatever2");
        producer.send("test", message2);

        Assertions.assertEquals(1, channel.messageCount("test"));
        String s = (String) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());

        Assertions.assertEquals(1, channel.messageCount("test"));
        Assertions.assertEquals("whatever", s);

    }


    @Test
    public void produceStringMessage() throws Exception {
        producer.send("test","whatever");
        producer.send("test","whatever2");

        verify(producer).send("test", ObjectByteConverter.encodeObject("whatever"));
        verify(producer).send("test", ObjectByteConverter.encodeObject("whatever2"));

        String s = (String) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());
        Assertions.assertEquals("whatever",s);

    }


    @Test
    public void produceJSONMessage() throws Exception {

        HashMap<String,String> hm = new HashMap<>();
        hm.put("key1","value1");
        hm.put("key2","value2");
        JSONObject json = new JSONObject(hm);
        JSONMessage message = new JSONMessage(json);

        producer.send("test", message);
        verify(producer).send("test", ObjectByteConverter.encodeObject(message));

        message = (JSONMessage) ObjectByteConverter.decodeObject(channel.basicGet("test",false).getBody());
        Assertions.assertEquals("value1", message.getJSON().get("key1"));
        Assertions.assertEquals("value2", message.getJSON().get("key2"));
    }

    @Test
    public void produceWrongTypeMessage_ThrowException() throws ProtocolIOException {
        assertThrows( ProtocolIOException.class , () -> {
            HashMap<String,String> hm = new HashMap<>();
            hm.put("key1","value1");
            hm.put("key2","value2");
            JSONObject json = new JSONObject();

            producer.send("test", json);
        });
    }

}