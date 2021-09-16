package my.spring.sample.mvc.utils;

import com.google.common.collect.Maps;
import my.spring.sample.mvc.model.PageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseUtil {

    public static Object message(String message) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("message", message);
        return map;
    }

    public static Object data(Object data) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("data", data);
        return map;
    }

    public static ResponseEntity<?> ok(String message) {
        return new ResponseEntity<>(
                message(message),
                HttpStatus.OK);
    }

    public static Object retFields(int status, Object data, PageInfo page) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("data", data);
        map.put("page", page);
        return map;
    }

    public static ResponseEntity<?> ok(Object data) {
        return new ResponseEntity<>(
                data(data),
                HttpStatus.OK);
    }

    public static ResponseEntity<?> ok(Object data, PageInfo page) {
        return new ResponseEntity<>(
                retFields(200, data, page),
                HttpStatus.OK);
    }

    public static ResponseEntity<?> ok(Object data, Object page) {
        return new ResponseEntity<>(
                retFields(200, data, (PageInfo)page),
                HttpStatus.OK);
    }

    public static ResponseEntity<?> badRequest(String message) {
        return new ResponseEntity<>(
                message(message),
                HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<?> internalServerError(String message) {
        return new ResponseEntity<>(
                message(message),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<?> getewayTimeout(String message) {
        return new ResponseEntity<>(
                message(message),
                HttpStatus.GATEWAY_TIMEOUT);
    }
}
