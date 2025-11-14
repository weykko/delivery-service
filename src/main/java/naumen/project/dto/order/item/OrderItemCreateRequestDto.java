package naumen.project.dto.order.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Пункт в составе заказа. Используется для создания заказа
 *
 * @param menuItemId id пункта меню
 * @param quantity   количество
 */
public record OrderItemCreateRequestDto(

        @NotNull
        @Min(1)
        Long menuItemId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
