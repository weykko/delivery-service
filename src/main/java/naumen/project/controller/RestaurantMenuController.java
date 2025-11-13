package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.menu.CreateMenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.mapper.MenuMapper;
import naumen.project.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления меню ресторана.
 * Предоставляет endpoints для создания, обновления и удаления блюд в меню.
 * Требует аутентификации с JWT токеном и права доступа RESTAURANT.
 *
 * @see MenuService
 * @see MenuMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/restaurant")
public class RestaurantMenuController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    public RestaurantMenuController(MenuService menuService, MenuMapper menuMapper) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
    }

    /**
     * Создает новое блюдо в меню ресторана текущего пользователя.
     *
     * @param request данные для создания позиции меню
     * @param user    аутентифицированный ресторан
     * @return созданное блюдо с присвоенным идентификатором
     */
    @PostMapping("/menu")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public MenuItemResponseDto createMenuItem(@RequestBody @Valid CreateMenuItemRequestDto request,
                                              @AuthenticationPrincipal User user) {
        MenuItem menuItem = menuService.createMenuItem(menuMapper.toEntity(request), user);
        return menuMapper.toResponse(menuItem);
    }

    /**
     * Обновляет существующее блюдо в меню ресторана.
     *
     * @param id      идентификатор обновляемой позиции меню
     * @param request данные для обновления позиции меню
     * @param user    аутентифицированный ресторан
     * @return обновленное блюдо
     */
    @PutMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public MenuItemResponseDto updateMenuItem(@PathVariable Long id,
                                              @RequestBody @Valid UpdateMenuItemRequestDto request,
                                              @AuthenticationPrincipal User user) {
        MenuItem menuItem = menuService.updateMenuItem(id, request, user);
        return menuMapper.toResponse(menuItem);
    }

    /**
     * Удаляет блюдо из меню ресторана.
     *
     * @param id   идентификатор удаляемой позиции меню
     * @param user аутентифицированный ресторан
     */
    @DeleteMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteMenuItem(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        menuService.deleteMenuItem(id, user);
    }
}
