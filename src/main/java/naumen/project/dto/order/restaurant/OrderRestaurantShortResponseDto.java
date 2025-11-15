package naumen.project.dto.order.restaurant;

import java.math.BigDecimal;

/**
 * DTO ответа с общей информацией о заказе для ресторана.
 *
 * @param id         идентификатор заказа
 * @param status     статус заказа
 * @param totalPrice общая стоимость заказа
 */
public record OrderRestaurantShortResponseDto(
        Long id,
        String status,
        BigDecimal totalPrice
) {
}
