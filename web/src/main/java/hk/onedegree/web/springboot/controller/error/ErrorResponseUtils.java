package hk.onedegree.web.springboot.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ErrorResponseUtils {

    public static ResponseEntity<Object> wrapException(Exception e, String errorCode, HttpStatus httpStatus){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now(ZoneId.of("UTC")));
        body.put("message", e.getMessage());
        body.put("error", errorCode);

        return new ResponseEntity<>(body, httpStatus);
    }
}
