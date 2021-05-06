package com.cymetrics.transactionmail;

import com.cymetrics.transactionmail.exceptions.GenerateHtmlContentFailed;
import com.cymetrics.transactionmail.exceptions.InitTemplateRendererFailed;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRenderer {

    private static TemplateRenderer instance;
    private static Logger logger = LoggerFactory.getLogger(TemplateRenderer.class);

    static {
        instance = new TemplateRenderer();
    }



    private Template resetPasswordTemplate;
    private Template emailVerificationTemplate;

    private TemplateRenderer() throws InitTemplateRendererFailed {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
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
        } catch (TemplateNotFoundException e) {
            String errMessage = String.format("Unable to find template: %s", e.getTemplateName());
            logger.error(errMessage);
            throw new InitTemplateRendererFailed(errMessage);
        } catch (MalformedTemplateNameException e) {
            String errMessage = String.format(String.format("Malformed template name: %s, %s", e.getTemplateName(), e.getMalformednessDescription()));
            logger.error(errMessage);
            throw new InitTemplateRendererFailed(errMessage);
        } catch (ParseException e) {
            String errMessage = String.format("Unable to parse template %s: %s", e.getTemplateName(), e.getMessage());
            logger.error(errMessage);
            throw new InitTemplateRendererFailed(errMessage);
        } catch (IOException e) {
            String errMessage = String.format("IO Exception raised", e.getMessage());
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
        data.put("name", name);
        data.put("verifyLink", verifyLink);
        data.put("title", "Reset password for your Cymetrics account");

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
        data.put("name", name);
        data.put("verifyCode", verifyCode);
        data.put("title", "Verify your Cymetrics account");

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

}
