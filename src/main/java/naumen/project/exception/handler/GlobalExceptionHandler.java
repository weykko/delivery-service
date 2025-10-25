package naumen.project.exception.handler;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import naumen.project.dto.error.ErrorResponseDto;
import naumen.project.dto.error.ViolationConstraintDto;
import naumen.project.exception.MenuItemNotFoundException;
import naumen.project.exception.WebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Глобальный обработчик ошибок
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Общий обработчик ошибок
     */
    @Hidden
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception ex, HttpServletRequest request) {
        log.error("Internal error", ex);
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                request.getServletPath(),
                null
        );
    }

    /**
     * Обработка {@link WebException}
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleWebException(WebException ex, HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getServletPath(),
                null
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * Обработка ошибок валидации
     */
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Входные данные не соответствуют заданным ограничениям",
                request.getServletPath(),
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> new ViolationConstraintDto(error.getField(), error.getDefaultMessage()))
                        .toList()
        );
    }


    @ExceptionHandler({
            MenuItemNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(RuntimeException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getServletPath(),
                null
        );
    }
}
