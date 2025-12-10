package naumen.project.exception;

/**
 * Исключение, возникающее при попытке доступа к несуществующей сущности.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String format, Object... args) {
        super(String.format(format, args));
    }
}
