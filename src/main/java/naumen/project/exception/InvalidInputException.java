package naumen.project.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String format, Object... args) {
        super(String.format(format, args));
    }
}
