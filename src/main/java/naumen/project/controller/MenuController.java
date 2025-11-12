package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Size;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.MenuItem;
import naumen.project.mapper.MenuMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с меню ресторанов.
 * Предоставляет endpoint для получения списка блюд с фильтрацией и пагинацией.
 * Требует аутентификации с JWT токеном.
 *
 * @see MenuService
 * @see MenuMapper
 * @see PageMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final PageMapper pageMapper;

    public MenuController(MenuService menuService, MenuMapper menuMapper, PageMapper pageMapper) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
        this.pageMapper = pageMapper;
    }

    /**
     * Получает список позиций меню с возможностью фильтрации и пагинации.
     *
     * @param restaurantId идентификатор ресторана для фильтрации
     * @param title текст для поиска по названиям блюд
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return страница с результатами поиска
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated
    @Transactional(readOnly = true)
    public PagedResponseDto<MenuItemResponseDto> getMenuItems(
            @RequestParam(required = false) Long restaurantId,
            @Size(max = 30)
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MenuItemResponseDto> menuPages = menuService
                .getMenuItems(restaurantId, title, PageRequest.of(page, size))
                .map(menuMapper::toResponse);
        return pageMapper.toMenuResponse(menuPages);
    }

    /**
     * Получает информацию о конкретной позиции меню по её идентификатору.
     *
     * @param menuId идентификатор позиции меню
     * @return информация о позиции меню
     */
    @GetMapping("/{menuId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public MenuItemResponseDto getMenuItem(@PathVariable Long menuId) {
        MenuItem menuItem = menuService.getMenuItemById(menuId);
        return menuMapper.toResponse(menuItem);
    }
}
