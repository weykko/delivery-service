package naumen.project.dto.order.admin;

import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Краткая информация о заказе для админа
 *
 * @param id           идентификатор заказа
 * @param status       статус заказа
 * @param clientId     идентификатор клиента, оформившего заказ
 * @param restaurantId идентификатор ресторана, в котором оформлен заказ
 * @param courierId    идентификатор курьера, доставляющего заказ
 * @param totalPrice   общая стоимость заказа
 * @param createdAt    время создания заказа
 */
public record OrderAdminShortResponseDto(
        Long id,
        OrderStatus status,
        Long clientId,
        Long restaurantId,
        Long courierId,
        BigDecimal totalPrice,
        Instant createdAt
) {
}
