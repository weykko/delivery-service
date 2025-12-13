package naumen.project.dto.order.restaurant;

import naumen.project.dto.order.item.OrderItemResponseDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO ответа с полной информацией о заказе.
 *
 * @param id         идентификатор заказа
 * @param status     статус заказа
 * @param totalPrice общая стоимость заказа
 * @param courierId  идентификатор курьера
 * @param createdAt  время создания заказа
 * @param items      список пунктов заказа
 */
public record OrderRestaurantResponseDto(
        Long id,
        String status,
        BigDecimal totalPrice,
        Long courierId,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
}
