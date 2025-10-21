package naumen.project.controller;

import naumen.project.dto.menu.MenuItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MenuItemResponseDto> getMenu() {
        return null;
    }
}
