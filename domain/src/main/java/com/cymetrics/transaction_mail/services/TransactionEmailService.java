package com.cymetrics.transaction_mail.services;

import javax.inject.Inject;

import com.cymetrics.transaction_mail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.transaction_mail.exceptions.ReceiverNotFound;
import com.cymetrics.transaction_mail.exceptions.SendTransactionMailFailed;
import com.cymetrics.transaction_mail.interfaces.MailSender;
import com.cymetrics.transaction_mail.TemplateRenderer;
import com.cymetrics.transaction_mail.model.Receiver;
import com.cymetrics.transaction_mail.repository.ReceiverRepository;

import java.util.Optional;

// TODO: Handle domain url and project name
//  It's not decided yet whether those information should be coming along with event payload,
//  or we should query it inside of application layer.
public class TransactionEmailService {

    @Inject MailSender sender;
    @Inject ReceiverRepository receiverRepository;
    TemplateRenderer renderer = TemplateRenderer.getInstance();

    public void sendResetPasswordMail(String email, String token) throws SendTransactionMailFailed, ReceiverNotFound, GenerateHtmlContentFailed {
        Optional<Receiver> receiver = receiverRepository.getReceiverByEmail(email);

        if (receiver.isEmpty()) {
            throw new ReceiverNotFound("Reset password: Receiver not found");
        }

        String receiverName = receiver.get().getUserName();
        String htmlContent = renderer.renderResetPasswordMailContent(receiverName, token);
        String alternativeContent = String.format("Hello %s, please follow this link to reset your password: %s", receiverName, token);

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

}
