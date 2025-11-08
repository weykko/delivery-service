package naumen.project.dto.paged;

import java.util.List;

/**
 * Кастомный ответ с пагинацией
 */
public record PagedResponseDto<T>(
        /**
         * Список элементов на текущей странице
         */
        List<T> content,
        
        /**
         * Номер текущей страницы (начиная с 0)
         */
        int page,
        
        /**
         * Размер страницы (количество элементов на странице)
         */
        int size,
        
        /**
         * Общее количество элементов
         */
        long totalElements,
        
        /**
         * Общее количество страниц
         */
        int totalPages
) {
}
