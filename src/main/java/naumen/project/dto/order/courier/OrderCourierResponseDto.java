package naumen.project.dto.order.courier;

import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;

/**
 * DTO ответа с информацией о заказе для курьера.
 *
 * @param id                идентификатор заказа
 * @param status            статус заказа
 * @param restaurantAddress адрес ресторана
 * @param deliveryAddress   адрес доставки
 * @param totalPrice        общая стоимость заказа
 */
public record OrderCourierResponseDto(
        Long id,
        OrderStatus status,
        String restaurantAddress,
        String deliveryAddress,
        BigDecimal totalPrice
) {
}
