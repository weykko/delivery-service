package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.menu.CreateMenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.User;
import naumen.project.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления меню ресторана.
 * Предоставляет endpoints для создания, обновления и удаления блюд в меню.
 * Требует аутентификации с JWT токеном и права доступа RESTAURANT.
 *
 * @see MenuService
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    private final MenuService menuService;

    public RestaurantController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Создает новое блюдо в меню ресторана текущего пользователя.
     *
     * @param request данные для создания блюда
     * @param user аутентифицированный пользователь с ролью RESTAURANT
     * @return созданное блюдо с присвоенным идентификатором
     */
    @PostMapping("/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponseDto createMenuItem(@RequestBody @Valid CreateMenuItemRequestDto request,
                                              @AuthenticationPrincipal User user) {
        return menuService.createMenuItem(request, user);
    }

    /**
     * Обновляет существующее блюдо в меню ресторана.
     *
     * @param id идентификатор обновляемого блюда
     * @param request данные для обновления блюда
     * @param user аутентифицированный пользователь с ролью RESTAURANT
     * @return обновленное блюдо
     */
    @PutMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemResponseDto updateMenuItem(@PathVariable Long id,
                                              @RequestBody @Valid UpdateMenuItemRequestDto request,
                                              @AuthenticationPrincipal User user) {
        return menuService.updateMenuItem(id, request, user);
    }

    /**
     * Удаляет блюдо из меню ресторана.
     *
     * @param id идентификатор удаляемого блюда
     * @param user аутентифицированный пользователь с ролью RESTAURANT
     */
    @DeleteMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        menuService.deleteMenuItem(id, user);
    }
}
