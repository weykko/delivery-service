package naumen.project.dto.order.restaurant;

import naumen.project.entity.enums.OrderStatus;

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
        OrderStatus status,
        BigDecimal totalPrice
) {
}
