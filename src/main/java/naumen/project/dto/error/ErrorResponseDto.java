package naumen.project.dto.error;

import java.time.Instant;
import java.util.List;

/**
 * Ответ при ошибке
 *
 * @param timestamp  Время возникновения ошибки
 * @param status     HTTP статус код ошибки
 * @param error      Описание ошибки
 * @param path       URL путь запроса, вызвавшего ошибку
 * @param violations Список ошибок валидации (может быть пустым)
 */
public record ErrorResponseDto(

        Instant timestamp,

        Integer status,

        String error,

        String path,

        List<ViolationConstraintDto> violations
) {
}
