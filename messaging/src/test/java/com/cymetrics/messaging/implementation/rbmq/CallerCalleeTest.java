package com.cymetrics.messaging.implementation.rbmq;

import com.cymetrics.domain.messaging.IMessageCallback;
import com.cymetrics.domain.messaging.IMessageCallee;
import com.cymetrics.domain.messaging.IMessageCaller;
import com.cymetrics.domain.messaging.exceptions.ProtocolIOException;
import com.cymetrics.domain.messaging.exceptions.QueueLifecycleException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
public class CallerCalleeTest {

    static private GenericContainer rbmq;
    static MessageProxyRBMQImp proxy;
    @BeforeEach
    public void cleanQueue() throws QueueLifecycleException {
        proxy.cleanQueue("functionA");
        proxy.cleanQueue("functionB");
    }

    @BeforeAll
    public static void createQueue() throws Exception{
        rbmq = RBMQTestcontainer.getContainer();
        Integer mappedPort = rbmq.getMappedPort(5672);
        RBMQConfig config = new RBMQConfig("guest", "guest", "127.0.0.1", mappedPort);
        ChannelFactory.config = config;
        proxy = new MessageProxyRBMQImp();
        proxy.createQueue("functionA");
        proxy.createQueue("functionB");
    }

    @AfterAll
    public static void deleteQueue() throws Exception{
        proxy.deleteQueue("functionA");
        proxy.deleteQueue("functionB");
    }

    @Test
    public void basicCallerCallee(){
        try {
            MessageProxyRBMQImp proxy = new MessageProxyRBMQImp();

            proxy.cleanQueue("functionA");
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
