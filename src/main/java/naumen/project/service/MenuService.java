package naumen.project.service;

import naumen.project.dto.menu.MenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.exception.WebException;
import naumen.project.mapper.MenuMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final PageMapper pageMapper;

    public MenuService(MenuRepository menuRepository, MenuMapper menuMapper, PageMapper pageMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
        this.pageMapper = pageMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<MenuItemResponseDto> getMenuItems(Long restaurantId, String title, Pageable pageable) {
        Page<MenuItemResponseDto> menuPages = menuRepository
                .findByRestaurantIdAndTitle(restaurantId, title, pageable)
                .map(menuMapper::toResponse);

        return pageMapper.toResponse(menuPages);
    }

    @Transactional
    public MenuItemResponseDto createMenuItem(MenuItemRequestDto request, User user) {
        MenuItem menuItem = menuMapper.toEntity(request);
        menuItem.setRestaurant(user);

        menuRepository.save(menuItem);

        return menuMapper.toResponse(menuItem);
    }

    @Transactional
    public MenuItemResponseDto updateMenuItem(Long id, MenuItemRequestDto request, User user) {
        MenuItem menuItem = getMenuItemById(id);

        assertBelongsToRestaurant(menuItem, user);

        menuMapper.updateEntityFromRequest(request, menuItem);
        menuRepository.save(menuItem);

        return menuMapper.toResponse(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id, User user) {
        MenuItem menuItem = getMenuItemById(id);

        assertBelongsToRestaurant(menuItem, user);

        menuRepository.delete(menuItem);
    }

    private MenuItem getMenuItemById(Long id) {
        return menuRepository
                .findById(id)
                .orElseThrow(() -> new WebException(
                        HttpStatus.NOT_FOUND,
                        "Позиция меню с id '%d' не найдена",
                        id));
    }

    private void assertBelongsToRestaurant(MenuItem menuItem, User user) {
        if (menuItem.getRestaurant().equals(user)) {
            throw new WebException(
                    HttpStatus.FORBIDDEN,
                    "Позиция меню с id '%d' не принадлежит вашему ресторану",
                    menuItem.getId());
        }
    }
}