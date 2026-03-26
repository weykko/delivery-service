package naumen.project.dto.order.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import naumen.project.entity.enums.OrderStatus;

import java.math.BigDecimal;

/**
 * DTO запроса на обновление информации о заказе админом
 *
 * @param status     новый статус заказа
 * @param courierId  идентификатор курьера
 * @param totalPrice общая стоимость заказа
 */
public record OrderAdminUpdateRequestDto(

        OrderStatus status,

        @Min(1)
        Long courierId,

        @Positive
        @Max(1000000)
        BigDecimal totalPrice
) {
}
