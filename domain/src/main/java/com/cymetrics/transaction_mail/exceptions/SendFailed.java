package com.cymetrics.transaction_mail.exceptions;

public class SendFailed extends Exception  {
    public SendFailed(String errorMessage) { super(errorMessage); }
}
