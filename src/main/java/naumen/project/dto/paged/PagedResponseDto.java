package naumen.project.dto.paged;

import java.util.List;

/**
 * Кастомный ответ с пагинацией
 */
public record PagedResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
