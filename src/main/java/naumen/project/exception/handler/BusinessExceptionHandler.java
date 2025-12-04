package naumen.project.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import naumen.project.dto.error.ErrorResponseDto;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Глобальный обработчик исключений для REST API.
 * Обрабатывает бизнес-исключения и возвращает структурированные ответы.
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

    /**
     * Обработка исключения {@link EntityNotFoundException}
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getServletPath(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Обработка исключения {@link InvalidInputException}
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleInvalidInputException(InvalidInputException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getServletPath(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обработка исключения {@link PermissionCheckFailedException}
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handlePermissionCheckFailedException(PermissionCheckFailedException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getServletPath(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
