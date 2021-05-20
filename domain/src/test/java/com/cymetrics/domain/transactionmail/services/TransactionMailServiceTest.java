package com.cymetrics.domain.transactionmail.services;

import com.cymetrics.domain.transactionmail.services.common.TemplateRenderer;
import com.cymetrics.domain.transactionmail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.domain.transactionmail.exceptions.InvalidEmailFormat;
import com.cymetrics.domain.transactionmail.exceptions.ReceiverNotFound;
import com.cymetrics.domain.transactionmail.services.common.EmailSender;
import com.cymetrics.domain.transactionmail.aggregates.Receiver;
import com.cymetrics.domain.transactionmail.repository.ReceiverRepository;
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

    @Mock EmailSender mockSender;
    @Mock ReceiverRepository mockReceiverRepository;
    @Mock TemplateRenderer mockTemplateRenderer;

    TransactionEmailService transactionMailService;

    @BeforeEach
    public void setup() {
        transactionMailService = new TransactionEmailService();
        transactionMailService.mailSender = this.mockSender;
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
        Assertions.assertThrows(ReceiverNotFound.class, () -> {
            transactionMailService.sendWelcomeOnboardMail(email);
        });
        verifyNoInteractions(transactionMailService.mailSender);
    }

    @Test
    @DisplayName("Mail should not be sent if template renderer fails")
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

        when(this.mockTemplateRenderer.renderWelcomeOnBoardMailContent(any())).thenThrow(GenerateHtmlContentFailed.class);
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            transactionMailService.sendWelcomeOnboardMail(email);
        });

        verifyNoInteractions(transactionMailService.mailSender);
    }


    @Test
    @DisplayName("Make sure 'reset password' is sent correctly")
    public void send_reset_password_successfully() throws InvalidEmailFormat, GenerateHtmlContentFailed, ReceiverNotFound {
        String email = "din.djarin@mandalorian.com";
        String name = "Din Djarin";
        String token = "H4sSqyJx";
        Receiver user = new Receiver("1", email, name);
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));

        transactionMailService.sendResetPasswordMail(email, token);

        verify(this.mockSender, times(1)).sendHighLevelMail(
            argThat(payload -> {
                Assertions.assertArrayEquals(payload.getRecipients(), new String[] { email });
                Assertions.assertEquals(payload.getCc(), null);
                Assertions.assertEquals(payload.getBcc(), null);

                String[] mailContents = (new String[] {
                    payload.getHtmlContent(),
                    payload.getAlternativeContent()
                });

                for (String content : mailContents) {
                    Assertions.assertTrue(content.contains(name) && content.contains(token));
                }

                return true;
            })
        );
    }

    @Test
    @DisplayName("Make sure 'email verification' is sent correctly")
    public void send_email_verification_successfully() throws InvalidEmailFormat, GenerateHtmlContentFailed, ReceiverNotFound {
        String email = "grogu@mandalorian.com";
        String name = "Grogu";
        String token = "H4sSqyJx";
        Receiver user = new Receiver("1", email, name);
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));

        transactionMailService.sendEmailVerificationMail(email, token);

        verify(this.mockSender, times(1)).sendHighLevelMail(
            argThat(payload -> {
                Assertions.assertArrayEquals(payload.getRecipients(), new String[] { email });
                Assertions.assertEquals(payload.getCc(), null);
                Assertions.assertEquals(payload.getBcc(), null);

                String[] mailContents = (new String[] {
                    payload.getHtmlContent(),
                    payload.getAlternativeContent()
                });

                for (String content : mailContents) {
                    Assertions.assertTrue(content.contains(name) && content.contains(token));
                }

                return true;
            })
        );
    }


    @Test
    @DisplayName("Make sure 'welcome onboard' is sent correctly")
    public void send_welcome_onboard_successfully() throws InvalidEmailFormat, GenerateHtmlContentFailed, ReceiverNotFound {
        String email = "grogu@mandalorian.com";
        String name = "Grogu";
        Receiver user = new Receiver("1", email, name);
        when(this.mockReceiverRepository.getReceiverByEmail(eq(email))).thenReturn(Optional.of(user));

        transactionMailService.sendWelcomeOnboardMail(email);

        verify(this.mockSender, times(1)).sendLowLevelMail(
            argThat(payload -> {
                Assertions.assertArrayEquals(payload.getRecipients(), new String[] { email });
                Assertions.assertEquals(payload.getCc(), null);
                Assertions.assertEquals(payload.getBcc(), null);

                String[] mailContents = (new String[] {
                    payload.getHtmlContent(),
                    payload.getAlternativeContent()
                });

                for (String content : mailContents) {
                    Assertions.assertTrue(content.contains(name));
                }

                return true;
            })
        );
    }
}
