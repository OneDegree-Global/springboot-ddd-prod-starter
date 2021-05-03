package com.cymetrics.transaction_mail.utils;

import com.cymetrics.transaction_mail.exceptions.GenerateHtmlContentFailed;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRenderer {

    private static TemplateRenderer instance;

    static {
        try {
            instance = new TemplateRenderer();
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred during initialization");
        }
    }

    private static Logger logger = LoggerFactory.getLogger(TemplateRenderer.class);

    private Template resetPasswordTemplate;
    private Template emailVerificationTemplate;

    private TemplateRenderer() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(TemplateRenderer.class, "/transaction_mail_templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        this.resetPasswordTemplate = cfg.getTemplate("ResetPassword.ftl");
        this.emailVerificationTemplate = cfg.getTemplate("EmailVerification.ftl");

    }

    public static TemplateRenderer getInstance() {
        return instance;
    }

    public String renderResetPasswordMailContent(String name, String verifyLink) throws GenerateHtmlContentFailed {
        try {
            Map<String, String> data = new HashMap();
            data.put("name", name);
            data.put("verifyLink", verifyLink);

            StringWriter result = new StringWriter();
            this.resetPasswordTemplate.process(data, result);
            return result.toString();
        } catch (TemplateException e) {
            logger.error("Template exception", e);
            throw new GenerateHtmlContentFailed("Failed generating Html content: [reset password]");
        } catch (IOException e) {
            logger.error("IO exception", e);
            throw new GenerateHtmlContentFailed("Failed generating Html content: [reset password]");
        }
    }

    public String renderEmailVerificationMailContent(String name, String verifyCode) throws GenerateHtmlContentFailed {

        Map<String, String> data = new HashMap();
        data.put("name", name);
        data.put("verifyCode", verifyCode);

        StringWriter result = new StringWriter();

        try {
            this.emailVerificationTemplate.process(data, result);
        } catch (TemplateException e) {
            logger.error("Template exception", e);
            throw new GenerateHtmlContentFailed("Failed generating Html content: [email verification]");
        } catch (IOException e) {
            logger.error("IO Exception", e);
            throw new GenerateHtmlContentFailed("Failed generating Html content: [email verification]");
        }
        return result.toString();
    }

}
