package naumen.project.dto.paged;

import java.util.List;

/**
 * Кастомный ответ с пагинацией
 *
 * @param content       Список элементов на текущей странице
 * @param page          Номер текущей страницы (начиная с 0)
 * @param size          Размер страницы (количество элементов на странице)
 * @param totalElements Общее количество элементов
 * @param totalPages    Общее количество страниц
 */
public record PagedResponseDto<T>(

        List<T> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
