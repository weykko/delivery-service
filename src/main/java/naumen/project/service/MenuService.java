package naumen.project.service;

import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface MenuService {

    Page<MenuItemResponseDto> getMenuItems(Long restaurantId, String title, Pageable pageable);

    MenuItemResponseDto createMenuItem(MenuItemRequestDto menuItemRequestDto);

    MenuItemResponseDto updateMenuItem(Long id, MenuItemRequestDto menuItemRequestDto);

    void deleteMenuItem(Long id);
}