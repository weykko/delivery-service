package naumen.project.dto.menu;

import java.math.BigDecimal;

/**
 * Ответ с информацией о позиции меню
 */
public record MenuItemResponseDto(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Long restaurantId
) {
}