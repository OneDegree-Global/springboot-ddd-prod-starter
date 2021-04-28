package com.cymetrics.web.springboot.controller.error;

public class ErrorCode {
    private ErrorCode() {
        throw new IllegalStateException("ErrorCode class");
    }

    public final static String CREATE_USER_FAILS = "auth-001";
    public final static String LOGIN_FAILS = "auth-002";
    public final static String USER_NOT_FOUND = "auth-003";
    public final static String AUTH_FAILS = "auth-004";
    public final static String GET_INFO_FAILS = "auth-005";
}
