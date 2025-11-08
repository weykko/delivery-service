package naumen.project.dto.menu;

import java.math.BigDecimal;

/**
 * Ответ с информацией о позиции меню
 *
 * @param id           Уникальный идентификатор позиции меню
 * @param title        Название позиции меню
 * @param description  Описание позиции меню
 * @param price        Цена позиции меню
 * @param restaurantId Идентификатор ресторана
 */
public record MenuItemResponseDto(

        Long id,

        String title,

        String description,

        BigDecimal price,

        Long restaurantId
) {
}