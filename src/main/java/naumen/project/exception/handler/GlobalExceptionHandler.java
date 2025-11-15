package naumen.project.exception.handler;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import naumen.project.dto.error.ErrorResponseDto;
import naumen.project.dto.error.ViolationConstraintDto;
import naumen.project.exception.AppBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Objects;

/**
 * Глобальный обработчик исключений для REST API.
 * Перехватывает и обрабатывает исключения на уровне всего приложения, возвращая структурированные ответы.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабатывает все непредвиденные исключения.
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
     * Обрабатывает кастомные исключения приложения {@link AppBaseException}
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleWebException(AppBaseException ex, HttpServletRequest request) {
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
     * Обрабатывает ошибки валидации данных запроса.
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

    /**
     * Обрабатывает нарушения ограничений валидации.
     */
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDto handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Входные данные не соответствуют заданным ограничениям",
                request.getServletPath(),
                ex.getConstraintViolations().stream()
                        .map(e -> new ViolationConstraintDto(e.getPropertyPath().toString(), e.getMessage()))
                        .toList()
        );
    }

    /**
     * Обрабатывает ошибки валидации параметров методов.
     */
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDto handleConstraintViolationException(HandlerMethodValidationException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Входные данные не соответствуют заданным ограничениям",
                request.getServletPath(),
                ex.getParameterValidationResults().stream()
                        .map(e -> new ViolationConstraintDto(
                                e.getMethodParameter().getParameterName(),
                                e.getResolvableErrors().getFirst().getDefaultMessage())
                        )
                        .toList()
        );
    }

    /**
     * Обрабатывает ошибки аутентификации.
     */
    @Hidden
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ErrorResponseDto handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Неверный логин / пароль",
                request.getServletPath(),
                null
        );
    }

    /**
     * Обрабатывает неподдерживаемые HTTP методы.
     */
    @Hidden
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponseDto handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "HTTP метод '%s' не поддерживается на этом эндпоинте".formatted(ex.getMethod()),
                request.getServletPath(),
                null
        );
    }

    /**
     * Обрабатывает ошибки чтения JSON запроса.
     */
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDto handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Неверный запрос JSON",
                request.getServletPath(),
                null
        );
    }

    /**
     * Обрабатывает несоответствие типов аргументов для параметров.
     */
    @Hidden
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Неверный тип для параметра '%s': ожидается тип '%s'"
                        .formatted(ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()),
                request.getServletPath(),
                null
        );
    }

    /**
     * Обрабатывает запросы к несуществующим ресурсам.
     */
    @Hidden
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponseDto handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Запрашиваемый ресурс не найден",
                request.getServletPath(),
                null
        );
    }
}
