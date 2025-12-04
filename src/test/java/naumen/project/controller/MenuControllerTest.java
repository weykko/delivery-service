package naumen.project.controller;

import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.MenuMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.MenuService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Модульные тесты для {@link MenuController}
 */
@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private MenuController menuController;

    /**
     * Тестирование получения списка позиций меню с фильтрацией по ресторану и названию
     */
    @Test
    void getMenuItems_WithFilters_ShouldReturnPagedResults() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);
        MenuItemResponseDto menuItemResponse = createMenuItemResponse(menuItem);

        Long restaurantId = 1L;
        String title = "Пицца";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<MenuItem> menuItemPage = new PageImpl<>(List.of(menuItem), pageable, 1);
        PagedResponseDto<MenuItemResponseDto> expectedPagedResponse = new PagedResponseDto<>(
                List.of(menuItemResponse),
                page,
                size,
                1,
                1
        );

        Mockito.when(menuService.getMenuItems(restaurantId, title, pageable)).thenReturn(menuItemPage);
        Mockito.when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponse);
        Mockito.when(pageMapper.toResponse(Mockito.any())).thenReturn(expectedPagedResponse);

        PagedResponseDto<MenuItemResponseDto> result = menuController.getMenuItems(restaurantId, title, page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.totalElements());
        Assertions.assertEquals(1, result.content().size());
        Mockito.verify(menuService).getMenuItems(restaurantId, title, pageable);
        Mockito.verify(pageMapper).toResponse(Mockito.any());
    }

    /**
     * Тестирование получения списка позиций меню без фильтров
     */
    @Test
    void getMenuItems_WithoutFilters_ShouldReturnAllResults() {
        User restaurantUser = createRestaurantUser(2L);
        MenuItem menuItem = createMenuItem(restaurantUser);
        MenuItemResponseDto menuItemResponse = createMenuItemResponse(menuItem);

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<MenuItem> menuItemPage = new PageImpl<>(List.of(menuItem), pageable, 1);
        PagedResponseDto<MenuItemResponseDto> expectedPagedResponse = new PagedResponseDto<>(
                List.of(menuItemResponse),
                page,
                size,
                1,
                1
        );

        Mockito.when(menuService.getMenuItems(null, null, pageable)).thenReturn(menuItemPage);
        Mockito.when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponse);
        Mockito.when(pageMapper.toResponse(Mockito.any())).thenReturn(expectedPagedResponse);

        PagedResponseDto<MenuItemResponseDto> result = menuController.getMenuItems(null, null, page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.totalElements());
        Mockito.verify(menuService).getMenuItems(null, null, pageable);
    }

    /**
     * Тестирование получения конкретной позиции меню по идентификатору
     */
    @Test
    void getMenuItem_WithValidId_ShouldReturnMenuItem() {
        User restaurantUser = createRestaurantUser(3L);
        MenuItem menuItem = createMenuItem(restaurantUser);
        MenuItemResponseDto menuItemResponse = createMenuItemResponse(menuItem);
        Long menuId = 1L;

        Mockito.when(menuService.getMenuItemById(menuId)).thenReturn(menuItem);
        Mockito.when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponse);

        MenuItemResponseDto result = menuController.getMenuItem(menuId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuItemResponse.id(), result.id());
        Assertions.assertEquals(menuItemResponse.title(), result.title());
        Assertions.assertEquals(menuItemResponse.price(), result.price());
        Mockito.verify(menuService).getMenuItemById(menuId);
        Mockito.verify(menuMapper).toResponse(menuItem);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя-ресторана
     */
    private User createRestaurantUser(Long id) {
        User user = new User("restaurant@example.com", "Test Restaurant", "+79991234567", Role.RESTAURANT);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }

    /**
     * Создает тестовый пункт меню
     */
    private MenuItem createMenuItem(User restaurant) {
        MenuItem item = new MenuItem("Pizza", "description", new BigDecimal(450));
        item.setId(1L);
        item.setRestaurant(restaurant);
        return item;
    }

    /**
     * Создает тестовый ответ с информацией о пункте меню
     */
    private MenuItemResponseDto createMenuItemResponse(MenuItem menuItem) {
        return new MenuItemResponseDto(
                menuItem.getId(),
                menuItem.getTitle(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getRestaurant().getId()
        );
    }
}

