package naumen.project.dto.menu;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MenuItemRequestDto(
        @NotBlank
        @Size(min = 3, max = 30)
        String title,

        @Size(max = 120)
        String description,

        @NotNull
        @Positive
        @Max(100000)
        BigDecimal price
) {
}
