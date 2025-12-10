package naumen.project.dto.error;

/**
 * Список ошибок валидации
 *
 * @param fieldName Имя поля с ошибкой валидации
 * @param message   Сообщение об ошибке валидации
 */
public record ViolationConstraintDto(

        String fieldName,

        String message
) {
}
