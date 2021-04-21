package com.odhk.mailService.constant;

public enum MailTemplateType {

    EMAIL_VERIFICATION("email_verification"),
    WELCOME_NEW_USER("welcome_new_user"),
    RESET_PASSWORD("reset_password"),
    GENERIC_MAIL_TYPE("generic_mail_type");

    private String type;
    MailTemplateType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
