package naumen.project.dto.order.client;

import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;

/**
 * Ответ клиенту по инфмаорции заказа при создании
 *
 * @param id         id заказа
 * @param status     статус заказа
 * @param totalPrice общая сумма заказа
 */
public record OrderClientShortResponseDto(
        Long id,
        OrderStatus status,
        BigDecimal totalPrice,
        String deliveryAddress
) {
}
