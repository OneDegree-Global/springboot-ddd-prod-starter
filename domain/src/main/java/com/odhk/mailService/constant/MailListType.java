package com.odhk.mailService.constant;

public enum MailListType {

    ALL_VERIFIED_USER("all_verified_user"),
    ALL_UNVERIFIED_USER("all_unverified_user"),
    ALL_BLOCKED_USER("all_blocked_user");

    private String type;
    MailListType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
