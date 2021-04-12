package hk.onedegree.web.springboot.controller.advice;

import hk.onedegree.application.exception.CreateUserFailsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CreateUserFailsExceptionController {
    @ExceptionHandler(value = CreateUserFailsException.class)
    public ResponseEntity<Object> exception(CreateUserFailsException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now(ZoneId.of("UTC")));
        body.put("message", e.getMessage());
        body.put("error", ErrorCode.CREATE_USER_FAILS);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
