package com.cymetrics.web.springboot.requestbody.quickstart;

import com.cymetrics.domain.messaging.types.JsonMessage;

public class MessagingRequest {
    String queueName;
    JsonMessage message;
}
