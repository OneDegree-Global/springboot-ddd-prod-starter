package hk.onedegree.web.springboot.controller.error;

import hk.onedegree.application.exception.CreateUserFailsException;
import hk.onedegree.application.exception.UnAuthorizeException;
import hk.onedegree.web.springboot.controller.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

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

}
