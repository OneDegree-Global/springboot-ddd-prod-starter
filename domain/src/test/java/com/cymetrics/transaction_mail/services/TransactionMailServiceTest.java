package com.cymetrics.transaction_mail.services;

import com.cymetrics.transaction_mail.TemplateRenderer;
import com.cymetrics.transaction_mail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.transaction_mail.exceptions.InvalidEmailFormat;
import com.cymetrics.transaction_mail.exceptions.ReceiverNotFound;
import com.cymetrics.transaction_mail.exceptions.SendTransactionMailFailed;
import com.cymetrics.transaction_mail.interfaces.MailSender;
import com.cymetrics.transaction_mail.model.Receiver;
import com.cymetrics.transaction_mail.repository.ReceiverRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionMailServiceTest {

    @Mock MailSender mockSender;
    @Mock ReceiverRepository mockReceiverRepository;
    @Mock TemplateRenderer mockTemplateRenderer;

    TransactionEmailService transactionMailService;

    @BeforeEach
    public void setup() {
        transactionMailService = new TransactionEmailService();
        transactionMailService.sender = this.mockSender;
        transactionMailService.receiverRepository = this.mockReceiverRepository;
    }

    @Test
    @DisplayName("Mail should not be sent if receiver is not found")
    public void receiver_not_found() {
        String email = "mike.trout@mlb.com";
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.empty());
        Assertions.assertThrows(ReceiverNotFound.class, () -> {
            transactionMailService.sendResetPasswordMail(email, "H4sSqyJx");
        });
        Assertions.assertThrows(ReceiverNotFound.class, () -> {
            transactionMailService.sendEmailVerificationMail(email, "H4sSqyJx");
        });
        verifyNoInteractions(transactionMailService.sender);
    }

    @Test
    @DisplayName("Mail should not be sent if template engine fails")
    public void template_engine_failed() throws InvalidEmailFormat, GenerateHtmlContentFailed {
        String email = "shohei.ohtani@mlb.com";
        String name = "Shohei Ohtani";
        Receiver user = new Receiver("1", email, name);
        transactionMailService.renderer = this.mockTemplateRenderer;
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));

        when(this.mockTemplateRenderer.renderResetPasswordMailContent(any(), any())).thenThrow(GenerateHtmlContentFailed.class);
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            transactionMailService.sendResetPasswordMail(email, "H4sSqyJx");
        });

        when(this.mockTemplateRenderer.renderEmailVerificationMailContent(any(), any())).thenThrow(GenerateHtmlContentFailed.class);
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            transactionMailService.sendEmailVerificationMail(email, "H4sSqyJx");
        });

        verifyNoInteractions(transactionMailService.sender);
    }


    @Test
    @DisplayName("Make sure 'reset password' is sent correctly")
    public void send_reset_password_successfully() throws InvalidEmailFormat, GenerateHtmlContentFailed, ReceiverNotFound, SendTransactionMailFailed {
        String email = "din.djarin@mandalorian.com";
        String name = "Din Djarin";
        String token = "H4sSqyJx";
        Receiver user = new Receiver("1", email, name);
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));
        transactionMailService.sendResetPasswordMail(email, token);
        verify(this.mockSender, times(1)).send(
                eq(new String[] { email }),
                eq(new String[] {}),
                eq(new String[] {}),
                anyString(),
                argThat(str -> str.contains(name) && str.contains(token)),
                argThat(str -> str.contains(name) && str.contains(token))
        );
    }

    @Test
    @DisplayName("Make sure 'email verification' is sent correctly")
    public void send_email_verification_successfully() throws InvalidEmailFormat, GenerateHtmlContentFailed, ReceiverNotFound, SendTransactionMailFailed {
        String email = "grogu@mandalorian.com";
        String name = "Grogu";
        String token = "H4sSqyJx";
        Receiver user = new Receiver("1", email, name);
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));
        transactionMailService.sendEmailVerificationMail(email, token);
        verify(this.mockSender, times(1)).send(
                eq(new String[] { email }),
                eq(new String[] {}),
                eq(new String[] {}),
                anyString(),
                argThat(str -> str.contains(name) && str.contains(token)),
                argThat(str -> str.contains(name) && str.contains(token))
        );
    }
}
