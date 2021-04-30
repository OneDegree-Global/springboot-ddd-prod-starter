package com.cymetrics.web.springboot.controller;

import com.cymetrics.application.exception.CreateUserFailsException;
import com.cymetrics.application.exception.RetrieveUserInfoFailsException;
import com.cymetrics.application.services.UserService;
import com.cymetrics.web.springboot.controller.error.ErrorCode;
import com.cymetrics.web.springboot.controller.utils.ResponseUtils;
import com.cymetrics.web.springboot.dto.User;
import com.cymetrics.web.springboot.requestbody.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
public class UserController {

    @Inject
    UserService userService;


    @PostMapping("/users")
    public ResponseEntity newUser(@RequestBody RegisterRequest registerRequest) throws CreateUserFailsException {
        var result = this.userService.createUser(registerRequest.getEmail(), registerRequest.getPassword());
        if(result.isEmpty()) {
            return ResponseUtils.wrapFailResponse("Empty user.", ErrorCode.CREATE_USER_FAILS);
        }

        User user = new User(result.get().getId(), result.get().getEmail());
        return ResponseUtils.wrapSuccessResponse(user);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUser(@RequestHeader("Authorization") String bearer, @PathVariable("id") String id) throws CreateUserFailsException, RetrieveUserInfoFailsException {
        var token = bearer.split("Bearer ")[1];
        var result = this.userService.getUser(token, id);
        if(result.isEmpty()) {
            return ResponseUtils.wrapFailResponse("User not found.", ErrorCode.USER_NOT_FOUND);
        }

        User user = new User(result.get().getId(), result.get().getEmail());
        return ResponseUtils.wrapSuccessResponse(user);
    }
}
