package naumen.project.dto.menu;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Создание позиции меню
 */
public record CreateMenuItemRequestDto(
        @NotBlank
        @Size(min = 3, max = 30)
        String title,

        @Size(max = 255)
        String description,

        @NotNull
        @Positive
        @Max(100000)
        BigDecimal price
) {
}
