package naumen.project.exception;

import org.springframework.http.HttpStatus;

public class IllegalDataException extends AppBaseException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public IllegalDataException(String message) {
        super(STATUS, message);
    }

    public IllegalDataException(String format, Object... args) {
        super(STATUS, format, args);
    }
}
