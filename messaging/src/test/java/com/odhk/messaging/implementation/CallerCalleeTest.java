package com.odhk.messaging.implementation;

import com.odhk.messaging.Exceptions.ProtocolIOException;
import com.odhk.messaging.Exceptions.QueueLifecycleException;
import com.odhk.messaging.IMessageCallback;
import com.odhk.messaging.IMessageCallee;
import com.odhk.messaging.IMessageCaller;
import com.odhk.messaging.IMessageQueueProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class CallerCalleeTest {

    @Test
    public void queueLifecycle(){
        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();
            proxy.deleteQueue("functionA");
            proxy.createQueue("functionA");

            proxy.deleteQueue("functionB");
            proxy.createQueue("functionB");
            Assertions.assertEquals(2, proxy.getQueueList().size() );
        } catch(QueueLifecycleException e){
            Assertions.fail("Create Queue Fail");
        }
    }

    @Test
    public void basicCallerCallee(){
        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();

            proxy.createQueue("functionA");
            proxy.cleanQueue("functionA");

            proxy.createQueue("functionB");
            proxy.cleanQueue("functionB");

            Thread calleeThread = new Thread(() -> {
                try {
                    IMessageCallee callee = new MessageCalleeRBMQImp();

                    Optional<String> tagA = callee.consumeAndReply("functionA", new IMessageCallback() {
                        @Override
                        public Object onCalled(Object arguments) {
                            Assertions.assertEquals("Message 1", (String) arguments);
                            return "functionA reply";
                        }

                    });
                    Optional<String> tagB = callee.consumeAndReply("functionB", new IMessageCallback() {
                        @Override
                        public Object onCalled(Object arguments) {
                            Assertions.assertEquals("Message 2", (String) arguments);
                            return "functionB reply";
                        }
                    });

                    Thread.sleep(3000);
                    callee.removeCallback(tagA.get());
                    // functionA Not able to reply message anymore
                } catch (QueueLifecycleException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });

            Thread callerThread = new Thread(() -> {
                try {
                    IMessageCaller caller = new MessageCallerRBMQImp();

                    Thread.sleep(1000);

                    Optional<Object> reply = caller.sendAndGetReply("functionA", "Message 1", 2000);
                    if (reply.isEmpty()) {
                        Assertions.fail("Should reply not be empty");
                    } else {
                        Assertions.assertEquals("functionA reply", (String)reply.get());
                    }

                    reply = caller.sendAndGetReply("functionB", "Message 2", 2000);
                    if (reply.isEmpty()) {
                        Assertions.fail("Should reply not be empty");
                    } else {
                        Assertions.assertEquals("functionB reply", (String)reply.get());
                    }

                    Thread.sleep(4000);

                    reply = caller.sendAndGetReply("functionA", "Message 3", 2000);
                    if (reply.isPresent()) {
                        Assertions.fail("Should reply not be any message");
                    }

                } catch (QueueLifecycleException | ProtocolIOException| InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            });

            calleeThread.start();
            callerThread.start();
            Thread.sleep(6000);
        }catch ( QueueLifecycleException | InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }
}
