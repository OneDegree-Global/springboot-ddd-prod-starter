package com.cymetrics.notification.email.smtp;

import lombok.Data;

@Data
public class SMTPConfig {
    private String userName;
    private String password;
    private String host;
    private int port;

}
