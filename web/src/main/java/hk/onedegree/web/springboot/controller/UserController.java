package hk.onedegree.web.springboot.controller;

import hk.onedegree.application.services.OtherService;
import hk.onedegree.application.services.UserService;
import hk.onedegree.web.springboot.requestbody.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class UserController {

    @Inject
    OtherService otherService;

    @Inject
    UserService userService;

    @PostMapping("/users")
    public User newUser(@RequestBody User user) {
        if (this.userService.createUser(user.getEmail(), user.getPassword())) {
            return user;
        }

        return new User();
    }
}
