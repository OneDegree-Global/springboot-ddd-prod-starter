package hk.onedegree.web.springboot.requestbody;

import lombok.Data;

@Data
public class User {
    private String password = "";
    private String email = "";
}
