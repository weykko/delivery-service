package naumen.project.mapper;

import naumen.project.dto.menu.CreateMenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.MenuItem;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью MenuItem и DTO.
 * Используется для создания, обновления и отображения позиций меню.
 */
@Mapper(componentModel = "spring")
public interface MenuMapper {

    /**
     * Преобразует DTO запроса в сущность MenuItem.
     *
     * @param request DTO с данными для создания позиции меню
     * @return сущность MenuItem
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    MenuItem toEntity(CreateMenuItemRequestDto request);

    /**
     * Преобразует сущность MenuItem в DTO ответа.
     *
     * @param menuItem сущность позиции меню
     * @return DTO с данными позиции меню
     */
    @Mapping(target = "restaurantId",
            expression = "java(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null)")
    MenuItemResponseDto toResponse(MenuItem menuItem);

    /**
     * Обновляет сущность MenuItem данными из DTO запроса.
     *
     * @param request DTO с обновленными данными
     * @param menuItem сущность для обновления
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateMenuItemRequestDto request, @MappingTarget MenuItem menuItem);
}
