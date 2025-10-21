package naumen.project.dto.menu;

import java.math.BigDecimal;

public record MenuItemRequestDto(
        String title,
        String description,
        BigDecimal price
) {
}
