package hk.onedegree.web.springboot.controller;

import hk.onedegree.application.exception.CreateUserFailsException;
import hk.onedegree.application.services.UserService;
import hk.onedegree.web.springboot.controller.utils.ResponseUtils;
import hk.onedegree.web.springboot.dto.User;
import hk.onedegree.web.springboot.requestbody.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class UserController {

    @Inject
    UserService userService;

    @PostMapping("/users")
    public ResponseEntity newUser(@RequestBody RegisterRequest registerRequest) throws CreateUserFailsException {
        var result = this.userService.createUser(registerRequest.getEmail(), registerRequest.getPassword());
        User user = new User(result.getId(), result.getEmail());
        return ResponseUtils.wrapSuccessResponse(user);
    }
}
