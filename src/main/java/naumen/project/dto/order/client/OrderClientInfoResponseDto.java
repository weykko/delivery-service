package naumen.project.dto.order.client;

import naumen.project.dto.order.item.OrderItemResponseDto;
import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Полная информация о заказе. Выдается клиенту при его запросе
 * @param id id заказа
 * @param status статус заказа
 * @param totalPrice общая сумма заказа
 * @param createdAt дата создания заказа
 * @param restaurantId id ресторана, где оформлен заказ
 * @param courierId id курьера. Может быть null, если ещё не в доставке
 * @param items состав заказа
 */
public record OrderClientInfoResponseDto(
        Long id,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        Long restaurantId,
        Long courierId,
        List<OrderItemResponseDto> items
) {
}
