package naumen.project.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppBaseException {

    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException(String message) {
        super(STATUS, message);
    }

    public ForbiddenException(String format, Object... args) {
        super(STATUS, format, args);
    }
}
