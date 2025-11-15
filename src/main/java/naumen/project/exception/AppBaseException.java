package naumen.project.exception;

import org.springframework.http.HttpStatus;

public abstract class AppBaseException extends RuntimeException {

    private final HttpStatus status;

    protected AppBaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    protected AppBaseException(HttpStatus status, String format, Object... args) {
        this(status, String.format(format, args));
    }

    public HttpStatus getStatus() {
        return status;
    }
}
