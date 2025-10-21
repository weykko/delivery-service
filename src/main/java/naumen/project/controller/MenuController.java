package naumen.project.controller;

import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MenuItemResponseDto> getMenuItems(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 10) Pageable pageable) {
        return menuService.getMenuItems(restaurantId, title, pageable);
    }
}
