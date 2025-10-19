package naumen.project.dto.error;

import java.time.Instant;
import java.util.List;

/**
 * Ответ при ошибке
 */
public record ErrorResponseDto(
        Instant timestamp,
        Integer status,
        String error,
        String path,
        List<ViolationConstraintDto> violations
) {
}
