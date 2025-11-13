package naumen.project.dto.order.item;

import java.math.BigDecimal;

/**
 * DTO пункта заказа.
 *
 * @param menuItemId    идентификатор блюда из меню
 * @param menuItemTitle название блюда из меню
 * @param quantity      количество штук блюда
 * @param price         стоимость выбранного блюда
 */
public record OrderItemResponseDto(
        Long menuItemId,
        String menuItemTitle,
        Integer quantity,
        BigDecimal price
) {
}
