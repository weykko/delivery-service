package naumen.project.mapper;

import naumen.project.dto.order.admin.OrderAdminResponseDto;
import naumen.project.dto.order.admin.OrderAdminShortResponseDto;
import naumen.project.dto.order.client.OrderClientResponseDto;
import naumen.project.dto.order.client.OrderClientShortResponseDto;
import naumen.project.dto.order.courier.OrderCourierResponseDto;
import naumen.project.dto.order.item.OrderItemResponseDto;
import naumen.project.dto.order.restaurant.OrderRestaurantResponseDto;
import naumen.project.dto.order.restaurant.OrderRestaurantShortResponseDto;
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
     * Преобразует сущность Order в короткий DTO ответа для ресторана.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для ресторана
     */
    OrderRestaurantShortResponseDto toRestaurantShortResponse(Order order);

    /**
     * Преобразует сущность Order в DTO ответа для ресторана.
     *
     * @param order сущность заказа
     * @return общий DTO с данными заказа
     */
    @Mapping(target = "courierId", source = "courier.id")
    OrderRestaurantResponseDto toRestaurantResponse(Order order);

    /**
     * Преобразует сущность Order в ответ для клиента. Подробный вариант
     *
     * @param order сущность заказа
     * @return DTO ответа клиенту
     */
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "items", source = "items")
    OrderClientResponseDto toClientResponse(Order order);

    /**
     * Преобразует сущность Order в ответ для клиента. Короткий вариант
     *
     * @param order сущность заказа
     * @return DTO ответа клиенту
     */
    OrderClientShortResponseDto toClientShortResponse(Order order);

    /**
     * Преобразует сущность Order в DTO ответа для админа.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для админа
     */
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    OrderAdminResponseDto toAdminResponse(Order order);

    /**
     * Преобразует сущность Order в короткий DTO ответа для админа.
     *
     * @param order сущность заказа
     * @return DTO с данными заказа для админа
     */
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    OrderAdminShortResponseDto toAdminShortResponse(Order order);

    /**
     * Преобразует сущность OrderItem в DTO ответа.
     * Нужен для маппинга Order в response
     *
     * @param orderItem элемент заказа
     * @return DTO элемента заказа
     */
    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "menuItemTitle", source = "menuItem.title")
    @Mapping(target = "price", source = "itemPrice")
    OrderItemResponseDto toOrderItemResponse(OrderItem orderItem);
}
