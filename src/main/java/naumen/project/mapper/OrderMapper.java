package naumen.project.mapper;

import naumen.project.dto.order.OrderCourierResponseDto;
import naumen.project.dto.order.OrderItemResponseDto;
import naumen.project.dto.order.OrderResponseDto;
import naumen.project.dto.order.OrderRestaurantResponseDto;
import naumen.project.dto.order.client.OrderClientCreateResponseDto;
import naumen.project.dto.order.client.OrderClientInfoResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между сущностью Order и различными DTO.
 * Используется для создания ответов для курьеров, ресторанов и общих ответов о заказах.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Преобразует сущность Order в DTO ответа для курьера.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для курьера
     */
    @Mapping(target = "restaurantAddress", source = "restaurant.address")
    OrderCourierResponseDto toCourierResponse(Order order);

    /**
     * Преобразует сущность Order в DTO ответа для ресторана.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для ресторана
     */
    OrderRestaurantResponseDto toRestaurantResponse(Order order);

    /**
     * Преобразует сущность Order в общий DTO ответа.
     *
     * @param order сущность заказа
     * @return общий DTO с данными заказа
     */
    @Mapping(target = "courierId", source = "courier.id")
    OrderResponseDto toResponse(Order order);

    /**
     * Преобразует сущность Order в ответ для клиента. Подробный вариант
     *
     * @param order сущность заказа
     * @return DTO ответа клиенту
     */
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "items", source = "items")
    OrderClientInfoResponseDto toClientInfoResponse(Order order);

    /**
     * Преобразует сущность Order в ответ для клиента при оформлении заказа.
     *
     * @param order сущность заказа
     * @return DTO ответа клиенту
     */
    @Mapping(target = "items", source = "items")
    OrderClientCreateResponseDto toClientCreateResponse(Order order);

    /**
     * Преобразует сущность OrderItem в DTO ответа.
     * Нужен для метода toResponse(Order order)
     *
     * @param orderItem элемент заказа
     * @return DTO элемента заказа
     */
    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "menuItemTitle", source = "menuItem.title")
    @Mapping(target = "price", source = "itemPrice")
    OrderItemResponseDto toOrderItemResponse(OrderItem orderItem);
}
