package com.cymetrics.domain.transactionmail.services.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cymetrics.domain.transactionmail.exceptions.GenerateHtmlContentFailed;
import freemarker.template.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateRendererTest {

    @Mock Template mockTemplate;
    @Mock TemplateException mockTemplateException;
    @Mock IOException mockIOException;
    ListAppender<ILoggingEvent> appender;
    Logger logger = (Logger) LoggerFactory.getLogger(TemplateRenderer.class);

    TemplateRenderer renderer;

    @BeforeEach
    void setup() {
        this.appender = new ListAppender<>();
        this.logger.addAppender(this.appender);
        this.appender.start();
        renderer = new TemplateRenderer();
        renderer.resetPasswordTemplate = mockTemplate;
        renderer.emailVerificationTemplate = mockTemplate;
        renderer.welcomeOnBoardTemplate = mockTemplate;
    }

    @Test
    @DisplayName("During render process, make sure template exception is handled and correctly logged")
    public void handle_template_exception() throws TemplateException, IOException, GenerateHtmlContentFailed {

        doThrow(mockTemplateException).when(mockTemplate).process(any(), any());

        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderWelcomeOnBoardMailContent("");
        });
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderResetPasswordMailContent("", "");
        });
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderEmailVerificationMailContent("", "");
        });

        List<ILoggingEvent> logs = appender.list;
        Assertions.assertEquals(3, logs.size());

        for (ILoggingEvent log : logs) {
            Assertions.assertEquals(Level.ERROR, log.getLevel());
        }
    }

    @Test
    @DisplayName("During render process, make sure io exception is handled and correctly logged")
    public void handle_IO_exception() throws TemplateException, IOException, GenerateHtmlContentFailed {

        doThrow(mockIOException).when(mockTemplate).process(any(), any());

        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderWelcomeOnBoardMailContent("");
        });
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderResetPasswordMailContent("", "");
        });
        Assertions.assertThrows(GenerateHtmlContentFailed.class, () -> {
            renderer.renderEmailVerificationMailContent("", "");
        });

        List<ILoggingEvent> logs = appender.list;
        Assertions.assertEquals(3, logs.size());

        for (ILoggingEvent log : logs) {
            Assertions.assertEquals(Level.ERROR, log.getLevel());
        }
    }
}
