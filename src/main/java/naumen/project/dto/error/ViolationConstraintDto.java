package naumen.project.dto.error;

/**
 * Список ошибок валидации
 */
public record ViolationConstraintDto(
        /**
         * Имя поля с ошибкой валидации
         */
        String fieldName,
        
        /**
         * Сообщение об ошибке валидации
         */
        String message
) {
}
