package naumen.project.mapper;

import naumen.project.dto.menu.MenuItemResponseDto;
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
    PagedResponseDto<MenuItemResponseDto> toResponse(Page<MenuItemResponseDto> request);
}
