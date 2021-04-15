package com.odhk.messaging.implementation.rbmq;

import com.odhk.messaging.IMessageProducer;
import com.odhk.messaging.exceptions.ProtocolIOException;
import com.odhk.messaging.implementation.rbmq.ChannelFactory;
import com.odhk.messaging.implementation.rbmq.MessageProducerRBMQImp;
import com.odhk.messaging.implementation.utils.ObjectByteConverter;
import com.odhk.messaging.messageTypes.JSONMessage;
import com.rabbitmq.client.Channel;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.Spy;

import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ProducerTest {

    IMessageProducer producer;
    static  Channel channel;

    @BeforeEach
    public void initChannelQueue() throws Exception {
        channel.queuePurge("test");
        producer = spy(new MessageProducerRBMQImp());
    }

    @BeforeAll
    static public void createQueue() throws Exception {
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
