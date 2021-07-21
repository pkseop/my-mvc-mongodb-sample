package my.spring.sample.mvc.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;
    private Object errors;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String error, Object errors, int status, String path) {
        this.message = message;
        this.error = error;
        this.errors = errors;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
