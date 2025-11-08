package naumen.project.dto.menu;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Создание позиции меню
 */
public record UpdateMenuItemRequestDto(
        /**
         * Название позиции меню (от 3 до 30 символов)
         */
        @Size(min = 3, max = 30)
        String title,

        /**
         * Описание позиции меню (максимум 255 символов)
         */
        @Size(max = 255)
        String description,

        /**
         * Цена позиции меню (до 100000 включительно)
         */
        @Positive
        @Max(100000)
        BigDecimal price
) {
}

