package naumen.project.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO ответа с полной информацией о заказе.
 *
 * @param id              идентификатор заказа
 * @param status          статус заказа
 * @param totalPrice      общая стоимость заказа
 * @param deliveryAddress адрес доставки
 * @param courierId       идентификатор курьера
 * @param createdAt       время создания заказа
 * @param items           список пунктов заказа
 */
public record OrderResponseDto(
        Long id,
        String status,
        BigDecimal totalPrice,
        String deliveryAddress,
        Long courierId,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
}
