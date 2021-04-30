package com.cymetrics.messaging.implementation.rbmq;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RBMQTestcontainer {

    static public GenericContainer rbmq;

    static public GenericContainer getContainer(){
        if(rbmq==null){
            rbmq = new GenericContainer(DockerImageName.parse("rabbitmq:3.7"))
                    .withExposedPorts(5672);
            rbmq.start();
        }
        return rbmq;
    }


}

