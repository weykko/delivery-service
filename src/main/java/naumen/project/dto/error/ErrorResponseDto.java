package naumen.project.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Ответ при ошибке
 *
 * @param timestamp  Временная метка ошибки
 * @param error      Сообщение об ошибке
 * @param path       Путь, по которому произошла ошибка
 * @param violations Список ошибок валидации
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        Instant timestamp,
        String error,
        String path,
        List<ViolationConstraintDto> violations
) {
}
