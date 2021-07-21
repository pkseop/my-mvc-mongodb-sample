package my.spring.sample.mvc.exception;

public class BadRequestException extends Exception {

    public BadRequestException(String msg) {
        super(msg);
    }
}
