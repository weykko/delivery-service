package naumen.project.controller;

import jakarta.validation.Valid;
import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.entity.User;
import naumen.project.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    private final MenuService menuService;

    public RestaurantController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponseDto createMenuItem(@RequestBody @Valid MenuItemRequestDto menuItemRequestDto,
                                              @AuthenticationPrincipal User user) {
        return menuService.createMenuItem(menuItemRequestDto, user);
    }

    @PutMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemResponseDto updateMenuItem(@RequestBody @Valid MenuItemRequestDto menuItemRequestDto,
                                              @PathVariable Long id,
                                              @AuthenticationPrincipal User user) {
        return menuService.updateMenuItem(id, menuItemRequestDto, user);
    }

    @DeleteMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id,
                               @AuthenticationPrincipal User user) {
        menuService.deleteMenuItem(id, user);
    }
}
