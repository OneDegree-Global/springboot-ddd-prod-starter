package com.cymetrics.web.springboot.controller.quickstart;

import com.cymetrics.domain.messaging.*;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.domain.messaging.types.JsonMessage;
import com.cymetrics.web.springboot.controller.utils.ResponseUtils;
import com.cymetrics.web.springboot.requestbody.quickstart.MessagingRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@RestController
@ConditionalOnProperty(
        value = "launch.type",
        havingValue = "QUICKSTART_MESSAGING")
public class MessagingController {

    @Autowired
    @Qualifier("mqProducer")
    IMessageProducer producer;
    @Autowired
    @Qualifier("mqPublisher")
    IMessagePublisher publisher;
    @Autowired
    @Qualifier("mqProxy")
    IMessageQueueProxy proxy;
    @Autowired
    @Qualifier("mqConsumer")
    IMessageConsumer consumer;
    @Autowired
    @Qualifier("mqSubscriber")
    IMessageSubscriber subscriber;


    @PostConstruct
    public void InitQueue() throws QueueLifecycleException {
        proxy.createQueue("quickstart_testing_queue1");
        proxy.createQueue("quickstart_testing_queue2");
        proxy.createTopic("quickstart_testing_topic");

        consumer.consume("quickstart_testing_queue1", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("queue1 consumer receive message:" + jsonMessage.getJSON().get("message"));
            }
        });

        subscriber.subscribe("quickstart_testing_topic", "quickstart_testing_queue1", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("queue1 consumer receive message:" + jsonMessage.getJSON().get("message"));
            }
        });
        subscriber.subscribe("quickstart_testing_topic", "quickstart_testing_queue2", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("queue2 consumer receive message:" + jsonMessage.getJSON().get("message"));
            }
        });

    }

    @PreDestroy
    public void CleanQueue() throws QueueLifecycleException {
        proxy.deleteQueue("quickstart_testing_queue");
        proxy.deleteTopic("quickstart_testing_topic");
    }

    @PostMapping("/messaging/{queueName}")
    public ResponseEntity produceMessage(@RequestBody MessagingRequest request, @PathVariable("queueName") String queueName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", request.getMessage());
        JsonMessage message = new JsonMessage(jsonObject);

        try {
            producer.send(queueName, message);
        } catch (ProtocolIOException e) {
            return ResponseUtils.wrapFailResponse("error when producing message to mq " + queueName, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseUtils.wrapSuccessResponse("produce to mq successfully");
    }

    @PostMapping("/messaging/publish/{queueName}")
    public ResponseEntity publishMessage(@RequestBody MessagingRequest request, @PathVariable("queueName") String queueName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", request.getMessage());
        JsonMessage message = new JsonMessage(jsonObject);

        try {
            publisher.publish(queueName, message);
        } catch (ProtocolIOException e) {
            return ResponseUtils.wrapFailResponse("error when producing message to mq " + queueName, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseUtils.wrapSuccessResponse("publish to mq successfully");
    }

}
