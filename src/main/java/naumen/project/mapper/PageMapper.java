package naumen.project.mapper;

import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.order.courier.OrderCourierResponseDto;
import naumen.project.dto.order.restaurant.OrderRestaurantShortResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

/**
 * Маппер для преобразования объектов пагинации.
 * Конвертирует Spring Page в кастомный DTO для пагинации.
 */
@Mapper(componentModel = "spring")
public interface PageMapper {

    /**
     * Преобразует Spring Page<MenuItemResponseDto> в кастомный DTO пагинации.
     *
     * @param request страница Spring с элементами MenuItemResponseDto
     * @return DTO с пагинированными данными
     */
    @Mapping(source = "number", target = "page")
    PagedResponseDto<MenuItemResponseDto> toMenuResponse(Page<MenuItemResponseDto> request);

    /**
     * Преобразует Spring Page<OrderCourierResponseDto> в кастомный DTO пагинации.
     *
     * @param request страница Spring с элементами OrderCourierResponseDto
     * @return DTO с пагинированными данными
     */
    @Mapping(source = "number", target = "page")
    PagedResponseDto<OrderCourierResponseDto> toOrderCourierResponse(Page<OrderCourierResponseDto> request);

    /**
     * Преобразует Spring Page<OrderRestaurantResponseDto> в кастомный DTO пагинации.
     *
     * @param request страница Spring с элементами OrderRestaurantResponseDto
     * @return DTO с пагинированными данными
     */
    @Mapping(source = "number", target = "page")
    PagedResponseDto<OrderRestaurantShortResponseDto> toOrderRestaurantResponse(Page<OrderRestaurantShortResponseDto> request);
}
