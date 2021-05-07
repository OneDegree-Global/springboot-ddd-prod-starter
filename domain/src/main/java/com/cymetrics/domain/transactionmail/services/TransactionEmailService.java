package com.cymetrics.domain.transactionmail.services;

import javax.inject.Inject;

import com.cymetrics.domain.transactionmail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.domain.transactionmail.exceptions.ReceiverNotFound;
import com.cymetrics.domain.transactionmail.exceptions.SendTransactionMailFailed;
import com.cymetrics.domain.transactionmail.interfaces.MailSender;
import com.cymetrics.domain.transactionmail.utils.TemplateRenderer;
import com.cymetrics.domain.transactionmail.aggregates.Receiver;
import com.cymetrics.domain.transactionmail.repository.ReceiverRepository;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle domain url and project name
//  It's not decided yet whether those information should be coming along with event payload,
//  or we should query it inside of application layer.
public class TransactionEmailService {

    @Inject MailSender sender;
    @Inject ReceiverRepository receiverRepository;
    TemplateRenderer renderer = TemplateRenderer.getInstance();

    private static Logger logger = LoggerFactory.getLogger(TransactionEmailService.class);

    public void sendResetPasswordMail(String email, String token) throws SendTransactionMailFailed, ReceiverNotFound, GenerateHtmlContentFailed {
        Optional<Receiver> receiver = receiverRepository.getReceiverByEmail(email);

        if (receiver.isEmpty()) {
            logger.error(String.format("Unable to find receiver by email: %s", email));
            throw new ReceiverNotFound("Reset password: Receiver not found");
        }
        String receiverName = receiver.get().getUserName();
        String htmlContent = renderer.renderResetPasswordMailContent(receiverName, token);
        String alternativeContent = String.format("Hello %s, please follow this link to reset your password: %s", receiverName, token);

        // TODO: failure cases handling
        this.sender.send(
            new String[] { receiver.get().getEmail() },
            new String[] {},
            new String[] {},
            String.format("Reset your password"),
            htmlContent,
            alternativeContent
        );
    }

    public void sendEmailVerificationMail(String email, String verifyCode) throws SendTransactionMailFailed, ReceiverNotFound, GenerateHtmlContentFailed {
        Optional<Receiver> receiver = receiverRepository.getReceiverByEmail(email);

        if (receiver.isEmpty()) {
            logger.error(String.format("Unable to find receiver by email: %s", email));
            throw new ReceiverNotFound("Email verification: Receiver not found");
        }

        String receiverName = receiver.get().getUserName();
        String htmlContent = renderer.renderEmailVerificationMailContent(receiverName, verifyCode);
        String alternativeContent = String.format("Hello %s, please follow this link to activate your account: %s", receiverName, verifyCode);

        this.sender.send(
            new String[] { receiver.get().getEmail() },
            new String[] {},
            new String[] {},
            String.format("Account verification"),
            htmlContent,
            alternativeContent
        );
    }

    public void sendWelcomeOnBoardMail(String email) throws ReceiverNotFound, GenerateHtmlContentFailed, SendTransactionMailFailed {
        Optional<Receiver> receiver = receiverRepository.getReceiverByEmail(email);

        if (receiver.isEmpty()) {
            logger.error(String.format("Unable to find receiver by email: %s", email));
            throw new ReceiverNotFound("Welcome on board: Receiver not found");
        }

        String receiverName = receiver.get().getUserName();
        String htmlContent = renderer.renderWelcomeOnBoardMailContent(receiverName);
        String alternativeContent = String.format(
                "Hello %s, welcome to Cymetrics. We've got you some useful tips, please follow this link to check them out.",
                receiverName
        );

        this.sender.send(
                new String[] { receiver.get().getEmail() },
                new String[] {},
                new String[] {},
                String.format("Welcome to Cymetrics"),
                htmlContent,
                alternativeContent
        );

    }

}
