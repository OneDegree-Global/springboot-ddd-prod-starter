# Messaging Interface

The Messaging interface is defined in the domain module.
And its corresponding implementation is provided in the messaging module.
Currently, we support RabbitMQ Implementation.

### IMessageCallback

The unified interface for providing callback to a consumer. 
Pass the callback to corresponding consumer function,\
then the consumer would call it when receiving a messsage.

``` java
IMessageCallback callback = ;
consumer.Consume("Queue a", new IMessageCallbkac(){ /* Override corresponding methods */ });
```

### IMeesageQueueProxy
A proxy interface for asking Messaging server to manage queue & topic
supported operations includes create / delete / clean queues \
and create / delete topic.

``` java
IMessageQueueProxy proxy; // Injected by DI
proxy.createQueue("Onedegree");
proxy.deleteQueue("Onedegree");
```


<hr style="border:2px solid gray"> </hr>

### IMessageConsumer
The most basic type of message consumer.
By passing callback to the consumer, the worker thread will listening on the specified queue\
until manually remove. \
It is **Non-blocking**, which means your thread will continue after the call to consumer.


### IMessageProducer
The most basic type of message producer.
By passing Text, Byte array or Object to the produce function, the corresponding consumer listening\
on the same queue will invoke its callback. It is **non-blocking**, which means your thread will continue executing after
"produce" call.



### IMessageReceiver
IMessageReceiver fetches a message from the queue. It is **Blocking**, 
which means you will get the message object from the "receive" call. It does not use callback function.

```java
IMessageProxy proxy; // Injected by DI
IMessageProducer producer;
IMessageConsumer consumer;

proxy.createQueue("onedegree");
consumer.consume("onedegree",  new IMessageCallback() {
    @Override
    public void onDelivered(Object message) {
        System.out.println((String)message);
    }
} );

producer.produce("onedegree", "whatever message");

// Console log: whatever message
```


<hr style="border:2px solid gray"> </hr>


### IMessageSubscriber
It behaves like **IMessageConsumer** (**Non-blocking** when called). \
But it listens to a topic rather than a specific queue, which is published by an **IMessagePublisher**.
All IMessageSubscriber who subscribes to the same topic will got the message.
When subscribing a consumer to a topic, you should provide both the topic name and the queue tp receive the message.



### IMessagePublisher
The counterpart ot **IMessageSubscriber**. Publish a message on a topic is like producing a message to a queue. \
Despite that you should create a exchange before sending to it.

``` java
IMessageProxy proxy; // Injected by DI
IMessageSubscriber subscriber;
IMessagePublisher publisher;

proxy.createQueue("onedegree");
proxy.createQueue("twodegree");
proxy.createExchange("publish_me");

subscriber.subscribe("publish_me", "onedegree", new IMessageCallback() {
    @Override
    public void onDelivered(Object message) {
        System.out.println((String)message+"1");
    }
});
subscriber.subscribe("publish_me", "onedegree", new IMessageCallback() {
    @Override
    public void onDelivered(Object message) {
        System.out.println((String)message+"2");
    }
});

publisher.publish("publish_me", "Message");

// Console output: 
// Message1
// Message2
```

<hr style="border:2px solid gray"> </hr>


### IMessageCaller
Deal with the message by the pattern of RPC. The caller will send a message to a specific queue for a
callee to respond, and block the thread until the replied message is ready in its own reply queue.
It also supports timeout.  It is **Blocking**. 


### IMessageCallee
The callee will consume a message from a queue, and reply immediately to another "reply queue" specified by the caller.
```java
IMessageProxy proxy; // Injected by DI
IMessageCaller caller;
IMessageCallee callee;

callee.consumeAndReply("onedegree", new IMessageCallback() {
    @Override
    public Object onCalled(Object message) {
        return "replied " + (String)message;
    }
});

String s = (String) caller.sendAndGetReply("onedegree", "whatever");
System.out.println(s);
// Console output:
// replied whatever
```

***

