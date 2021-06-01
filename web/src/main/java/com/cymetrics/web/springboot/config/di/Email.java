package com.cymetrics.web.springboot.config.di;

import org.springframework.context.annotation.Configuration;

import com.cymetrics.notification.email.smtp.SMTPConfig;

@Configuration
public class Email {

    public SMTPConfig smtpConfigBean() {
        SMTPConfig config = new SMTPConfig();
        config.setSenderAddress("no-reply@cymetrics.io");

        // the following info are unknown for now, to test the infra we use mailtrap instead.
        config.setHost("127.0.0.1");
        config.setPort(2525);
        config.setUserName("");
        config.setPassword("");

        return config;
    }

}
