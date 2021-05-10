package com.cymetrics.messaging.implementation.rbmq;

public class RBMQConfig {
    private String userName;
    private String password;
    private String host;
    private int port;


    public RBMQConfig(String userName, String password, String host, int port) {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
