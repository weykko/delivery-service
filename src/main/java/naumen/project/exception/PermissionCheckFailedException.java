package naumen.project.exception;

/**
 * Исключение, возникающее при неудачной проверке прав доступа
 */
public class PermissionCheckFailedException extends RuntimeException {
    public PermissionCheckFailedException(String message) {
        super(message);
    }

    public PermissionCheckFailedException(String format, Object... args) {
        super(String.format(format, args));
    }
}
