package hk.onedegree.web.springboot.controller.advice;

import hk.onedegree.application.exception.CreateUserFailsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CreateUserFailsExceptionController {
    @ExceptionHandler(value = CreateUserFailsException.class)
    public ResponseEntity<Object> exception(CreateUserFailsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
