package com.cymetrics.web.springboot.controller.error;

import com.cymetrics.application.exception.CreateUserFailsException;
import com.cymetrics.application.exception.RetrieveUserInfoFailsException;
import com.cymetrics.application.exception.UnAuthorizeException;
import com.cymetrics.web.springboot.controller.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorResolver {
    @ExceptionHandler(value = CreateUserFailsException.class)
    public ResponseEntity<Object> createUserFailsException(CreateUserFailsException e) {
        return ResponseUtils.wrapException(e, ErrorCode.CREATE_USER_FAILS, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnAuthorizeException.class)
    public ResponseEntity<Object> unAuthorizeException(UnAuthorizeException e) {
        return ResponseUtils.wrapException(e, ErrorCode.AUTH_FAILS, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = RetrieveUserInfoFailsException.class)
    public ResponseEntity<Object> retrieveUserInfoFailsException(RetrieveUserInfoFailsException e) {
        return ResponseUtils.wrapException(e, ErrorCode.GET_INFO_FAILS, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
