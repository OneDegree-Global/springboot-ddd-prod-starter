package com.cymetrics.web.springboot.requestbody.quickstart;

import com.cymetrics.domain.messaging.types.JsonMessage;
import lombok.Data;

@Data
public class MessagingRequest {
    String message;
}
