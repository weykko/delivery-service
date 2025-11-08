package naumen.project.dto.menu;

import java.math.BigDecimal;

/**
 * Ответ с информацией о позиции меню
 */
public record MenuItemResponseDto(
        /**
         * Уникальный идентификатор позиции меню
         */
        Long id,
        
        /**
         * Название позиции меню
         */
        String title,
        
        /**
         * Описание позиции меню
         */
        String description,
        
        /**
         * Цена позиции меню
         */
        BigDecimal price,
        
        /**
         * Идентификатор ресторана
         */
        Long restaurantId
) {
}