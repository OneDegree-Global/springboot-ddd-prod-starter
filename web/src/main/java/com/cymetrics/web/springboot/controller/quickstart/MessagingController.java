package com.cymetrics.web.springboot.controller.quickstart;

import com.cymetrics.application.exception.CreateUserFailsException;
import com.cymetrics.application.exception.RetrieveUserInfoFailsException;
import com.cymetrics.domain.messaging.*;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import com.cymetrics.domain.messaging.types.JsonMessage;
import com.cymetrics.web.springboot.controller.error.ErrorCode;
import com.cymetrics.web.springboot.controller.utils.ResponseUtils;
import com.cymetrics.web.springboot.dto.User;
import com.cymetrics.web.springboot.requestbody.RegisterRequest;
import com.cymetrics.web.springboot.requestbody.quickstart.MessagingRequest;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;


@RestController
@ConditionalOnProperty(
        value="launch.type",
        havingValue = "QUICKSTART_MESSAGING")
public class MessagingController {

    IMessageProducer producer;
    IMessagePublisher publisher;
    IMessageQueueProxy proxy;
    IMessageConsumer consumer;
    IMessageSubscriber subscriber;

    @Inject
    public MessagingController(IMessageProducer producer,
                               IMessagePublisher publisher,
                               IMessageQueueProxy proxy,
                               IMessageConsumer consumer,
                               IMessageSubscriber subscriber) {
        this.producer = producer;
        this.publisher = publisher;
        this.proxy = proxy;
        this.consumer = consumer;
        this.subscriber = subscriber;
    }


    @PostConstruct
    public void InitQueue() throws QueueLifecycleException {
        proxy.createQueue("quickstart_testing_queue");
        proxy.createTopic("quickstart_testing_topic");
        consumer.consume("quickstart_testing_queue", new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("consumer receive message:"+jsonMessage.getJSON().get("message"));
            }
        });

        subscriber.subscribe("quickstart_testing_topic", "quickstart_testing_queue",new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("subscriber1 receive message:"+jsonMessage.getJSON().get("message"));
            }
        });
        subscriber.subscribe("quickstart_testing_topic", "quickstart_testing_queue",new IMessageCallback() {
            @Override
            public void onDelivered(Object message) {
                JsonMessage jsonMessage = (JsonMessage) message;
                System.out.println("subscriber2 receive message:"+jsonMessage.getJSON().get("message"));
            }
        });

    }

    @PreDestroy
    public void CleanQueue() throws QueueLifecycleException {
        proxy.deleteQueue("quickstart_testing_queue");
        proxy.deleteTopic("quickstart_testing_topic");
    }

    @PostMapping("/messaging/{queueName}")
    public ResponseEntity produceMessage(@RequestBody MessagingRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",request.getMessage());
        JsonMessage message =  new JsonMessage(jsonObject);

        try {
            producer.send(request.getQueueName(), message);
        } catch(ProtocolIOException e) {
            return ResponseUtils.wrapFailResponse("error when producing message to mq "+request.getQueueName(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseUtils.wrapSuccessResponse("produce to mq successfully");
    }

    @PostMapping("/messaging/publish/{queueName}")
    public ResponseEntity publishMessage(@RequestBody MessagingRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",request.getMessage());
        JsonMessage message =  new JsonMessage(jsonObject);

        try {
            publisher.publish(request.getExchangeName(),message);
        } catch(ProtocolIOException e) {
            return ResponseUtils.wrapFailResponse("error when producing message to mq "+request.getQueueName(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseUtils.wrapSuccessResponse("publish to mq successfully");
    }

}
