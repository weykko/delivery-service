package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Size;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.service.MenuService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с меню ресторанов.
 * Предоставляет endpoint для получения списка блюд с фильтрацией и пагинацией.
 * Требует аутентификации с JWT токеном.
 *
 * @see MenuService
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
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
    public PagedResponseDto<MenuItemResponseDto> getMenuItems(
            @RequestParam(required = false) Long restaurantId,
            @Size(max = 30)
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return menuService.getMenuItems(restaurantId, title, PageRequest.of(page, size));
    }
}
