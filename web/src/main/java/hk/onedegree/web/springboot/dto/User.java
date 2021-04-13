package hk.onedegree.web.springboot.dto;

import lombok.Data;

@Data
public class User {
    private final String password;
    private final String email;
}
