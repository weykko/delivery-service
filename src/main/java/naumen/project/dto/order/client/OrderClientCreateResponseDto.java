package naumen.project.dto.order.client;

import naumen.project.dto.order.OrderItemResponseDto;
import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Ответ клиенту по инфмаорции заказа при создании
 * @param id id заказа
 * @param status статус заказа
 * @param totalPrice общая сумма заказа
 * @param createdAt дата оформления заказа
 * @param items состав заказа
 */
public record OrderClientCreateResponseDto(
        Long id,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
}
