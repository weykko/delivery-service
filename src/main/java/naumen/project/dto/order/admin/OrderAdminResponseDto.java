package naumen.project.dto.order.admin;

import naumen.project.dto.order.item.OrderItemResponseDto;
import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Полная информация о заказе для админа
 *
 * @param id              идентификатор заказа
 * @param status          статус заказа
 * @param clientId        идентификатор клиента, оформившего заказ
 * @param restaurantId    идентификатор ресторана, в котором оформлен заказ
 * @param courierId       идентификатор курьера, доставляющего заказ
 * @param totalPrice      общая стоимость заказа
 * @param deliveryAddress адрес доставки
 * @param createdAt       время создания заказа
 * @param items           список пунктов заказа
 */
public record OrderAdminResponseDto(
        Long id,
        OrderStatus status,
        Long clientId,
        Long restaurantId,
        Long courierId,
        BigDecimal totalPrice,
        String deliveryAddress,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
}

