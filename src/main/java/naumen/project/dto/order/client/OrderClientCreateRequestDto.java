package naumen.project.dto.order.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import naumen.project.dto.order.item.OrderItemCreateRequestDto;

import java.util.List;

/**
 * Запрос на оформление заказа
 * @param restaurantId id ресторана
 * @param deliveryAddress адрес доставки
 * @param items состав заказа
 */
public record OrderClientCreateRequestDto(

        @NotNull
        @Min(1)
        Long restaurantId,

        @Size(max = 255)
        @NotBlank
        String deliveryAddress,

        @NotNull
        @Size(min = 1)
        List<OrderItemCreateRequestDto> items

) {
}
