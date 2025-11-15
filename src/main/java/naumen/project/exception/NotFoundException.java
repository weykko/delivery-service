package naumen.project.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppBaseException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(STATUS, message);
    }

    public NotFoundException(String format, Object... args) {
        super(STATUS, format, args);
    }
}
