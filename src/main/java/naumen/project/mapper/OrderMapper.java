package naumen.project.mapper;

import naumen.project.dto.order.OrderCourierResponseDto;
import naumen.project.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Преобразует сущность Order в DTO ответа для курьера.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для курьера
     */
    @Mapping(target = "restaurantAddress", ignore = true)
    OrderCourierResponseDto toResponse(Order order);
}
