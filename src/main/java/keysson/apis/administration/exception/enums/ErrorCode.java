package keysson.apis.administration.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
