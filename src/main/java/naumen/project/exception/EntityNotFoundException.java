package naumen.project.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String format, Object... args) {
        super(String.format(format, args));
    }
}
