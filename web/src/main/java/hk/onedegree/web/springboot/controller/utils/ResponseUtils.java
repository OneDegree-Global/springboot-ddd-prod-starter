package hk.onedegree.web.springboot.controller.utils;

import hk.onedegree.domain.auth.aggregates.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtils {
    private static final ZoneId zoneId = ZoneId.of("UTC");

    public static ResponseEntity<Object> wrapException(Exception e, String errorCode, HttpStatus httpStatus){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now(zoneId));
        body.put("message", e.getMessage());
        body.put("error", errorCode);
        body.put("status", 1);

        return new ResponseEntity<>(body, httpStatus);
    }

    public static ResponseEntity<Object> wrapUser(User user){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", ZonedDateTime.now(zoneId));
        body.put("status", 0);
        body.put("data", user);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
