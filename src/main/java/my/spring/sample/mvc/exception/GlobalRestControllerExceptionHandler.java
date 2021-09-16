package my.spring.sample.mvc.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.utils.RequestUtil;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity validationExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex)  {
        log.info("[EXCEPTION] Method argument not valid. URI: [{}], method: [{}], error message: [{}], from: [{}]",
                new Object[]{request.getRequestURI(), request.getMethod(), ex.getMessage(), RequestUtil.getClientIp(request)});

        List<Map> errors = Lists.newArrayList();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            Map<String, String> error = Maps.newHashMap();

            error.put("field", fieldError.getField());
            error.put("defaultMessage", fieldError.getDefaultMessage());

            errors.add(error);
        });

        return new ResponseEntity(
                new ErrorResponse(
                        (String) errors.get(0).get("defaultMessage"),
                        "Argument not valid.",
                        errors,
                        400,
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            BadRequestException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity badRequestExceptionHandler(HttpServletRequest request, Exception ex) {
        log.info("[EXCEPTION] Bad request. URI: [{}], method: [{}], error message: [{}], from [{}]",
                new Object[]{request.getRequestURI(), request.getMethod(), ex.getMessage(), RequestUtil.getClientIp(request)});

        return new ResponseEntity(
                new ErrorResponse(
                        ex.getMessage(),
                        "Bad request",
                        null,
                        400,
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(HttpServletRequest request, Exception ex) {
        if( ex instanceof ClientAbortException ||
                (ex instanceof IOException && ex.getMessage().contains("Broken pipe"))
        ) {
            log.info("[EXCEPTION] URI: [{}], method: [{}], Error message [{}], from [{}]",
                    new Object[] {request.getRequestURI(), request.getMethod(), ex.getMessage(), RequestUtil.getClientIp(request)} );
        } else {
            log.error("Internal server error occurred. URI [{}], method: [{}], from [{}]",
                    new Object[]{request.getRequestURI(), request.getMethod(), RequestUtil.getClientIp(request), ex});
        }
        return new ResponseEntity(
                new ErrorResponse(
                        ex.getMessage(),
                        "Internal server error",
                        null,
                        500,
                        request.getRequestURI()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
