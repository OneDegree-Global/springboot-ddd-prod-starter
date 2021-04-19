package hk.onedegree.web.springboot.requestbody;

import lombok.Data;

@Data
public class LoginRequest {
    private final String password;
    private final String email;
}
