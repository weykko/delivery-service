package naumen.project.mapper;

import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    MenuItem toEntity(MenuItemRequestDto request);

    @Mapping(target = "restaurantId",
            expression = "java(menuItem.getRestaurant() != null ? menuItem.getRestaurant().getId() : null)")
    MenuItemResponseDto toResponse(MenuItem menuItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    void updateEntityFromRequest(MenuItemRequestDto request, @MappingTarget MenuItem menuItem);
}
