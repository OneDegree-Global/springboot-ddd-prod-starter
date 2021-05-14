package com.cymetrics.notification.email.smtp;

import com.cymetrics.domain.transactionmail.interfaces.IMailSender;

import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MailSenderImpl implements IMailSender {

    @Inject SMTPConfig config;
    Transport transport;
    Session session;

    public MailSenderImpl() throws MessagingException {

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.transport.port", this.config.getPort());


        // mailtrap test
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); //it’s optional in Mailtrap
        props.put("mail.smtp.host", "smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");// use one of the options in the SMTP settings tab in your Mailtrap Inbox

        this.session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("67eedaef0606e8", "7e9006014b0c92");
                }
            }
        );

        this.transport = this.session.getTransport();
        this.transport.connect();
    }

    public void send(EmailSenderPayload payload) throws SendTransactionMailFailed {

        System.out.println("start");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()));


        try {
            if (!this.transport.isConnected()) {
                System.out.println("connecting....");
                this.transport.connect();
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("to_username_a@gmail.com, to_username_b@yahoo.com")
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Hello, I'm testing the mail");
            this.transport.sendMessage(message, message.getAllRecipients());
            System.out.println(dtf.format(LocalDateTime.now()));

            System.out.println("end");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SendTransactionMailFailed(String.format("Unable to establish connection, %s", e.getMessage()));
        }


    }

    public void send2(EmailSenderPayload payload) throws SendTransactionMailFailed, NoSuchProviderException {

        System.out.println("start");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()));

        Transport transport = this.session.getTransport();

        try {
            if (!transport.isConnected()) {
                System.out.println("connecting....");
                transport.connect();
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("to_username_a@gmail.com, to_username_b@yahoo.com")
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Hello, I'm testing the mail");
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println(dtf.format(LocalDateTime.now()));

            System.out.println("end");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SendTransactionMailFailed(String.format("Unable to establish connection, %s", e.getMessage()));
        }


    }

    public void testSend() throws SendTransactionMailFailed, NoSuchProviderException {
        this.send2(new EmailSenderPayload());
    }

}
