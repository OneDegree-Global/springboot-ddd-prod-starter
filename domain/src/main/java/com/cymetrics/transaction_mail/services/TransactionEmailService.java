package com.cymetrics.transaction_mail.services;

import javax.inject.Inject;

import com.cymetrics.transaction_mail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.transaction_mail.exceptions.ReceiverNotFound;
import com.cymetrics.transaction_mail.exceptions.SendFailed;
import com.cymetrics.transaction_mail.interfaces.MailSender;
import com.cymetrics.transaction_mail.utils.TemplateRenderer;
import com.cymetrics.transaction_mail.model.Receiver;
import com.cymetrics.transaction_mail.repository.ReceiversRepository;

import java.util.Optional;

public class TransactionEmailService {

    @Inject private MailSender sender;
    @Inject private ReceiversRepository receiversRepository;

    // Should be read from system configuration
    private String projectName = "Cymetrics";
    private String baseUrl = "https://staging.cymetrics.io";

    public void sendResetPasswordMail(String address, String token) throws SendFailed, ReceiverNotFound, GenerateHtmlContentFailed {
        TemplateRenderer renderer = TemplateRenderer.getInstance();
        Optional<Receiver> receiver = receiversRepository.getReceiverByAddress(address);

        if (receiver.isEmpty()) {
            throw new ReceiverNotFound("Reset password: Receiver not found");
        }

        String receiverName = receiver.get().getUserName();
        String verifyLink = String.format("%s/%s", baseUrl, token); // TODO: Should be derived from a solid method

        String htmlContent = renderer.renderResetPasswordMailContent(receiverName, verifyLink);

        String alternativeContent = String.format("Hello %s, please follow this link to reset your password: %s", receiverName, verifyLink);

        this.sender.send(
            new String[] { receiver.get().getAddress() },
            new String[] {},
            new String[] {},
            String.format("Reset your %s password", this.projectName),
            htmlContent,
            alternativeContent
        );
    }

    public void sendEmailVerificationMail(String address, String verifyCode) throws SendFailed, ReceiverNotFound, GenerateHtmlContentFailed {
        TemplateRenderer renderer = TemplateRenderer.getInstance();
        Optional<Receiver> receiver = receiversRepository.getReceiverByAddress(address);

        if (receiver.isEmpty()) {
            throw new ReceiverNotFound("Reset password: Receiver not found");
        }

        String receiverName = receiver.get().getUserName();
        String verifyLink = String.format("%s/%s", baseUrl, verifyCode); // TODO: Should be derived from a solid method

        String htmlContent = renderer.renderEmailVerificationMailContent(receiverName, verifyLink);

        String alternativeContent = String.format("Hello %s, please follow this link to activate your account: %s", receiverName, verifyLink);

        this.sender.send(
            new String[] { receiver.get().getAddress() },
            new String[] {},
            new String[] {},
            String.format("%s: Account verification", this.projectName),
            htmlContent,
            alternativeContent
        );
    }

}
