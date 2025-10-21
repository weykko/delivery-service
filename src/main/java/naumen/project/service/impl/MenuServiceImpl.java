package naumen.project.service.impl;

import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.entity.MenuItem;
import naumen.project.exception.MenuItemNotFoundException;
import naumen.project.mapper.MenuMapper;
import naumen.project.repository.MenuRepository;
import naumen.project.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponseDto> getMenuItems(Long restaurantId, String title, Pageable pageable) {
        return menuRepository
                .findByRestaurantIdAndTitle(restaurantId, title, pageable)
                .map(menuMapper::toResponse);
    }

    @Override
    @Transactional
    public MenuItemResponseDto createMenuItem(MenuItemRequestDto request) {
        MenuItem menuItem = menuMapper.toEntity(request);
//        menuItem.setRestaurant();

        menuRepository.save(menuItem);

        return menuMapper.toResponse(menuItem);
    }

    @Override
    @Transactional
    public MenuItemResponseDto updateMenuItem(Long id, MenuItemRequestDto request) {
        MenuItem menuItem = menuRepository
                .findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));

        menuMapper.updateEntityFromRequest(request, menuItem);
        menuRepository.save(menuItem);

        return menuMapper.toResponse(menuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuRepository
                .findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));

        menuRepository.delete(menuItem);
    }
}
