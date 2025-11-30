package naumen.project.exception;

public class PermissionCheckFailedException extends RuntimeException {
    public PermissionCheckFailedException(String message) {
        super(message);
    }

    public PermissionCheckFailedException(String format, Object... args) {
        super(String.format(format, args));
    }
}
