package naumen.project.mapper;

import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между сущностью MenuItem и DTO.
 * Используется для создания, обновления и отображения позиций меню.
 */
@Mapper(componentModel = "spring")
public interface MenuMapper {

    /**
     * Преобразует сущность MenuItem в DTO ответа.
     *
     * @param menuItem сущность позиции меню
     * @return DTO с данными позиции меню
     */
    @Mapping(target = "restaurantId",
            expression = "java(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null)")
    MenuItemResponseDto toResponse(MenuItem menuItem);
}
