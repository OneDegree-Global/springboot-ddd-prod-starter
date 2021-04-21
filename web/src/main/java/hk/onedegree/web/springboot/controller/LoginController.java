package hk.onedegree.web.springboot.controller;

import hk.onedegree.application.services.LoginService;
import hk.onedegree.web.springboot.controller.error.ErrorCode;
import hk.onedegree.web.springboot.controller.utils.ResponseUtils;
import hk.onedegree.web.springboot.dto.Token;
import hk.onedegree.web.springboot.requestbody.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class LoginController {
    @Inject
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity newUser(@RequestBody LoginRequest loginRequest) {
        var result = this.loginService.getLoginToken(loginRequest.getEmail(), loginRequest.getPassword());

        if (result.isEmpty() || result.get().length() == 0) {
            return ResponseUtils.wrapFailResponse("Login fails", ErrorCode.LOGIN_FAILS);
        }

        Token token = new Token(result.get());
        return ResponseUtils.wrapSuccessResponse(token);
    }
}
