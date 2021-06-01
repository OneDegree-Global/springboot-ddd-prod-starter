package com.cymetrics.notification.email.smtp;

import com.cymetrics.domain.transactionmail.interfaces.IMailSender;

import com.cymetrics.domain.transactionmail.services.EmailSenderPayload;

import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;


/*
    TODO: Implement connection pool for Transport object
    There will be a potential bottle neck if we have large amount of emails with only few "Transport" instances.
    "Transport" instance sends email one at a time to achieve thread-safe, which also implies that sending bulk mail is going to be inefficient.
    We can't even leverage multiple threads unless each thread has its own Transport instance but it takes about 2~4 seconds to create a new instance.
    Hence, a dynamic-sized connection pool will be needed when we're required to send large amount of mails.
 */
public class MailSenderImpl implements IMailSender {

    @Inject SMTPConfig config;
    Transport transport;
    Session session;

    private static String getAddressList(String[] addresses) {
        String addressList = "";
        if (addresses != null) {
            for (String recipient : addresses) {
                addressList += String.format("%s,", recipient);
            }
        }
        return addressList;
    }

    void connect() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", config.getPort());

        this.session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUserName(), config.getPassword());
                }
            }
        );

        this.transport = this.session.getTransport();
        this.transport.connect();
    }

    public MailSenderImpl() throws MessagingException {
        this.connect();
    }


    public void send(EmailSenderPayload payload) throws SendTransactionMailFailed {

        try {
            if (!this.transport.isConnected()) {
                this.transport.connect();
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getSenderAddress()));

            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(MailSenderImpl.getAddressList(payload.getRecipients()))
            );
            message.setRecipients(
                Message.RecipientType.CC,
                InternetAddress.parse(MailSenderImpl.getAddressList(payload.getCc()))
            );
            message.setRecipients(
                Message.RecipientType.BCC,
                InternetAddress.parse(MailSenderImpl.getAddressList(payload.getBcc()))
            );

            message.setSubject(payload.getSubject());

            Multipart multiPart = new MimeMultipart("alternative");
            MimeBodyPart alternativeText = new MimeBodyPart();
            alternativeText.setText(payload.getAlternativeContent(), "utf-8");
            MimeBodyPart htmlContent = new MimeBodyPart();
            htmlContent.setContent(payload.getHtmlContent(), "text/html; charset=utf-8");

            multiPart.addBodyPart(alternativeText);
            multiPart.addBodyPart(htmlContent);
            message.setContent(multiPart);

            this.transport.sendMessage(message, message.getAllRecipients());

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new SendTransactionMailFailed(String.format("Unable to send mail, %s", e.getMessage()));
        }

    }

}
