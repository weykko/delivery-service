package naumen.project.dto.error;

import java.time.Instant;
import java.util.List;

/**
 * Ответ при ошибке
 */
public record ErrorResponseDto(
        /**
         * Время возникновения ошибки
         */
        Instant timestamp,
        
        /**
         * HTTP статус код ошибки
         */
        Integer status,
        
        /**
         * Описание ошибки
         */
        String error,
        
        /**
         * URL путь запроса, вызвавшего ошибку
         */
        String path,
        
        /**
         * Список ошибок валидации (может быть пустым)
         */
        List<ViolationConstraintDto> violations
) {
}
