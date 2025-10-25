package naumen.project.dto.menu;

import java.math.BigDecimal;

public record MenuItemResponseDto(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Long restaurantId
) {
}