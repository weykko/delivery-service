package naumen.project.dto.menu;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Создание позиции меню
 *
 * @param title       Название позиции меню (от 3 до 30 символов)
 * @param description Описание позиции меню (максимум 255 символов)
 * @param price       Цена позиции меню (до 100000 включительно)
 */
public record UpdateMenuItemRequestDto(

        @Size(min = 3, max = 30)
        String title,

        @Size(max = 255)
        String description,

        @Positive
        @Max(100000)
        BigDecimal price
) {
}

