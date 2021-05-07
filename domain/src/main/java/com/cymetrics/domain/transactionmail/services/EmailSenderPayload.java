package com.cymetrics.domain.transactionmail.services;

import lombok.Data;

@Data
public class EmailSenderPayload {
    private String[] recipients;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String htmlContent;
    private String alternativeContent;
}
