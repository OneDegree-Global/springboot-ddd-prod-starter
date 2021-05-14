package com.cymetrics.domain.transactionmail.services.common;

import com.cymetrics.domain.transactionmail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.domain.transactionmail.exceptions.InitTemplateRendererFailed;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRenderer {

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    Logger logger = LoggerFactory.getLogger(TemplateRenderer.class);

    private static TemplateRenderer instance;

    static {
        instance = new TemplateRenderer();
    }

    static class CommonTemplateVariable {
        static String NAME = "name";
        static String TITLE = "title";
    }


    Template resetPasswordTemplate;
    Template emailVerificationTemplate;
    Template welcomeOnBoardTemplate;

    TemplateRenderer() throws InitTemplateRendererFailed {

        cfg.setClassForTemplateLoading(TemplateRenderer.class, "/transaction_mail_templates");

        // TODO: Check too see if we need to alter these default settings or not
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        try {
            this.resetPasswordTemplate = cfg.getTemplate("ResetPassword.ftl");
            this.emailVerificationTemplate = cfg.getTemplate("EmailVerification.ftl");
            this.welcomeOnBoardTemplate = cfg.getTemplate("WelcomeOnBoard.ftl");
        } catch (IOException e) {
            String errMessage = String.format("Template ERROR: %s", e.getMessage());
            logger.error(errMessage);
            throw new InitTemplateRendererFailed(errMessage);
        }
    }

    public static TemplateRenderer getInstance() {
        return instance;
    }

    private void templateExceptionHandler(TemplateException e) throws GenerateHtmlContentFailed {
        String errMessage = String.format("Failed render %s: %s", e.getTemplateSourceName(), e.getMessage());
        logger.error(errMessage);
        throw new GenerateHtmlContentFailed(errMessage);
    }

    public String renderResetPasswordMailContent(String name, String verifyLink) throws GenerateHtmlContentFailed {

        Map<String, String> data = new HashMap();
        data.put(CommonTemplateVariable.NAME, name);
        data.put(CommonTemplateVariable.TITLE, "Reset password for your Cymetrics account");
        data.put("verifyLink", verifyLink);

        StringWriter result = new StringWriter();

        try {
            this.resetPasswordTemplate.process(data, result);
        } catch (TemplateException e) {
            templateExceptionHandler(e);
        } catch (IOException e) {
            String errMessage = String.format("IO Exception for ResetPassword: %s", e.getMessage());
            logger.error(errMessage);
            throw new GenerateHtmlContentFailed(errMessage);
        }
        return result.toString();
    }

    public String renderEmailVerificationMailContent(String name, String verifyCode) throws GenerateHtmlContentFailed {

        Map<String, String> data = new HashMap();
        data.put(CommonTemplateVariable.NAME, name);
        data.put(CommonTemplateVariable.TITLE, "Verify your Cymetrics account");
        data.put("verifyCode", verifyCode);

        StringWriter result = new StringWriter();

        try {
            this.emailVerificationTemplate.process(data, result);
        } catch (TemplateException e) {
            templateExceptionHandler(e);
        } catch (IOException e) {
            String errMessage = String.format("IO Exception for EmailVerification: %s", e.getMessage());
            logger.error(errMessage);
            throw new GenerateHtmlContentFailed(errMessage);
        }
        return result.toString();
    }

    public String renderWelcomeOnBoardMailContent(String name) throws GenerateHtmlContentFailed {
        Map<String, String> data = new HashMap();
        data.put(CommonTemplateVariable.NAME, name);
        data.put(CommonTemplateVariable.TITLE, "Welcome to Cymetrics");
        StringWriter result = new StringWriter();

        try {
            this.welcomeOnBoardTemplate.process(data, result);
        } catch (TemplateException e) {
            templateExceptionHandler(e);
        } catch (IOException e) {
            String errMessage = String.format("IO Exception for EmailVerification: %s", e.getMessage());
            logger.error(errMessage);
            throw new GenerateHtmlContentFailed(errMessage);
        }
        return result.toString();
    }


}
