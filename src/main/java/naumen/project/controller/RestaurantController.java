package naumen.project.controller;

import jakarta.validation.Valid;
import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurant")
public class RestaurantController {

    private final MenuService menuService;

    public RestaurantController(MenuService menuService) {
        this.menuService = menuService;
    }

    //TODO: Блюдо должно создаваться для авторизованого ресторана, переделать после добавления авторизации
    @PostMapping("/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponseDto createMenuItem(@RequestBody @Valid MenuItemRequestDto menuItemRequestDto) {
        return menuService.createMenuItem(menuItemRequestDto);
    }

    @PutMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MenuItemResponseDto updateMenuItem(@RequestBody @Valid MenuItemRequestDto menuItemRequestDto,
                                              @PathVariable Long id) {
        return menuService.updateMenuItem(id,  menuItemRequestDto);
    }

    @DeleteMapping("/menu/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
    }
}
