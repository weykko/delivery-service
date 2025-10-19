package naumen.project.dto.error;

/**
 * Список ошибок валидации
 */
public record ViolationConstraintDto(String fieldName, String message) {
}
