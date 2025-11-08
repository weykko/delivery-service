package naumen.project.dto.error;

import java.time.Instant;
import java.util.List;

/**
 * Ответ при ошибке
 *
 * @param timestamp  Временная метка ошибки
 * @param status     HTTP статус ошибки
 * @param error      Сообщение об ошибке
 * @param path       Путь, по которому произошла ошибка
 * @param violations Список ошибок валидации
 */
public record ErrorResponseDto(
        Instant timestamp,
        Integer status,
        String error,
        String path,
        List<ViolationConstraintDto> violations
) {
}
