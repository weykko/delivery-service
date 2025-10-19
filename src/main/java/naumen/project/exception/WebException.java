package naumen.project.exception;

import org.springframework.http.HttpStatus;

/**
 * Ошибка для прокидывания наружу для клиента
 */
public class WebException extends RuntimeException {
    private HttpStatus status;

    /**
     * Инициализация с текстом ошибки
     */
    public WebException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Инициализация с форматируемым текстом ошибки
     */
    public WebException(HttpStatus status, String format, Object... args) {
        this(status, String.format(format, args));
    }

    public HttpStatus getStatus() {
        return status;
    }
}
