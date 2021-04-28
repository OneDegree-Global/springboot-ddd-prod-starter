package com.cymetrics.web.springboot.requestbody;

import lombok.Data;

@Data
public class RegisterRequest {
    private final String password;
    private final String email;
}
