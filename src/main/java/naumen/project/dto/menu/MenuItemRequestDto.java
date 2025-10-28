package naumen.project.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequestDto(
        @NotBlank
        @Size(min = 3, max = 30)
        String title,

        @Size(max = 120)
        String description,

        @NotNull
        @Positive
        BigDecimal price
) {
}
