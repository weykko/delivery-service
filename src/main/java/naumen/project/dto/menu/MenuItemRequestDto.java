package naumen.project.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequestDto(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 30, message = "Title must be between 3 and 30 characters")
        String title,

        @Size(max = 120, message = "Description should not exceed 120 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price
) {
}
