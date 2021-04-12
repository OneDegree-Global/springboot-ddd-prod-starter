package com.odhk.messaging.implementation;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.odhk.messaging.*;


class demoConsumerProducer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        IMessageQueueProxy proxy = new MessageProxyRBMQImp();
        proxy.deleteQueue("auth");
        proxy.createQueue("auth");
        proxy.createQueue("auth");
        proxy.createQueue("auth");

        proxy.deleteQueue("email");
        proxy.createQueue("email");


        // A application domain listening on a certain queue
        Thread consumerThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessageConsumer consumer = new MessageConsumerRBMQImp();

                    consumer.consumeOnce("auth", new IMessageCallback() {
                        @Override
                        public void onDelivered(Object message) {
                            System.out.println( "consume once on delivered : "+(String)message);
                        }
                        @Override
                        public void onCancel(){
                            System.out.println("consume once cancel");
                        }
                    });

                    consumer.consumeOnce("email", new IMessageCallback() {
                        @Override
                        public void onDelivered(Object message) {
                            System.out.println( "consume once on delivered : "+(String)message);
                        }
                        @Override
                        public void onCancel(){
                            System.out.println("consume once cancel");
                        }
                    });

                    Thread.sleep(3000);

                    String consumerTag = consumer.consume("auth", new IMessageCallback() {
                        @Override
                        public void onDelivered(Object message) {
                            System.out.println("consume on delivered :"+(String)message);
                        }
                        @Override
                        public void onCancel(){
                            System.out.println("consume cancel");
                        }
                    });

                    Thread.sleep(5000);
                    consumer.removeCallback(consumerTag);

                    // Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // another domain send message to that domain service
        Thread producerThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessageProducer producer = new MessageProducerRBMQImp();

                    producer.send("auth", "Sould be consume by ConsumeOnce auth");
                    //System.out.println("send "+ new String("Sould be consume by ConsumeOnce auth"));
                    producer.send("email", "Sould be consume by ConsumeOnce email");

                    producer.send("auth", "Sould be consume by Consumer");

                    Thread.sleep(3000);

                    producer.send("auth", "Message 1");
                    producer.send("auth", "Message 2");

                    Thread.sleep(7000);
                    producer.send("auth", "This Message should not appear");

                    // Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        System.out.println("start "+Thread.currentThread().getName()+" "+consumerThread.getName()+" "+producerThread.getName());
        consumerThread.start();
        producerThread.start();

    }
}


class demoPublisherSubscriber {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        IMessageQueueProxy proxy = new MessageProxyRBMQImp();
        proxy.createTopic("userAuthed");
        proxy.createTopic("mailSent");

        for(int i=1;i<=3;i++){
           // proxy.deleteQueue("userAuthedListener"+i);
            proxy.createQueue("userAuthedListener"+i);

           // proxy.deleteQueue("mailSentListener"+i);
            proxy.createQueue("mailSentListener"+i);
        }

        // A application domain listening on a certain queue
        Thread subscriberThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessageSubscriber subscriber = new MessageSubscriberRBMQImp();

                    for(int i=1;i<=3;i++){
                        final int j = i;
                        subscriber.subscribe("userAuthed","userAuthedListener"+j, new IMessageCallback() {

                            String queueName = "userAuthedListener"+j;
                            @Override
                            public void onDelivered(Object message) {
                                System.out.println( queueName+" "+(String)message);
                            }
                            @Override
                            public void onCancel(){
                                System.out.println( queueName+" stop listening");
                            }
                        });
                    }

                    for(int i=1;i<=3;i++){
                        final int j = i;
                        subscriber.subscribe("mailSent","mailSentListener"+j, new IMessageCallback() {

                            String queueName = "mailSentListener"+j;
                            @Override
                            public void onDelivered(Object message) {
                                System.out.println( queueName+" "+(String)message);
                            }
                            @Override
                            public void onCancel(){
                                System.out.println( queueName+" stop listening");
                            }
                        });
                    }

                    Thread.sleep(4000);

                    subscriber.unsubscribe("userAuthed", "userAuthedListener1");
                    subscriber.unsubscribe("userAuthed", "userAuthedListener3");

                    // Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // another domain send message to that domain service
        Thread publisherThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessagePublisher publisher = new MessagePublisherRBMQImp();

                    Thread.sleep(1000);

                    publisher.publish("userAuthed", "user is authenticated !");
                    //System.out.println("send "+ new String("Sould be consume by ConsumeOnce auth"));
                    publisher.publish("mailSent", "some email is sent !");
                    Thread.sleep(5000);

                    publisher.publish("userAuthed", "only listener 2 should receive this event");
                    // Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("start "+Thread.currentThread().getName()+" "+subscriberThread.getName()+" "+publisherThread.getName());
        subscriberThread.start();
        publisherThread.start();
    }
}

class demoCallerCallee {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        IMessageQueueProxy proxy = new MessageProxyRBMQImp();
        proxy.deleteQueue("functionA");
        proxy.createQueue("functionA");
        proxy.deleteQueue("functionB");
        proxy.createQueue("functionB");

        // A application make
        Thread calleeThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessageCallee callee = new MessageCalleeRBMQImp();

                    String tagA = callee.consumeAndReply("functionA", new IMessageCallback() {
                        @Override
                        public Object onCalled(Object arguments) {
                            System.out.println( "callee on delivered : "+ (String)arguments);
                            return "functionA reply";
                        }
                        @Override
                        public void onCancel(){
                            System.out.println("functionA cancel");
                        }
                    });
                    String tagB = callee.consumeAndReply("functionB", new IMessageCallback() {
                        @Override
                        public Object onCalled(Object arguments) {
                            System.out.println( "callee on delivered : "+ (String)arguments);
                            return "functionB reply";
                        }
                        @Override
                        public void onCancel(){
                            System.out.println("functionB cancel");
                        }
                    });

                    Thread.sleep(3000);
                    callee.removeCallback(tagA);
                    // functionA Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // another domain send message to that domain service
        Thread callerThread = new Thread( new Runnable(){
            @Override
            public void run(){
                try {
                    IMessageCaller caller = new MessageCallerRBMQImp();

                    Thread.sleep(1000);

                    Optional<Object> reply = caller.sendAndGetReply("functionA" , "Message 1", 2000);
                    if(reply.isEmpty()){
                        System.out.println("functionA no reply");
                    }else {
                        System.out.println("functionA reply: " + reply.get());
                    }
                    reply = caller.sendAndGetReply("functionB" , "Message 2", 2000);
                    if(reply.isEmpty()){
                        System.out.println("functionB no reply");
                    }else {
                        System.out.println("functionB reply: " + reply.get());
                    }
                    Thread.sleep(4000);

                    reply = caller.sendAndGetReply("functionA" , "Message 3", 2000);
                    if(reply.isPresent()) {
                        System.out.println("This should not appear");
                    }
                    // Not able to reply message anymore
                } catch (IOException | TimeoutException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("start "+Thread.currentThread().getName()+" "+callerThread.getName()+" "+ calleeThread.getName());
        calleeThread.start();
        callerThread.start();

    }
}
