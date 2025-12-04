package naumen.project.controller;

import naumen.project.dto.menu.CreateMenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.MenuMapper;
import naumen.project.service.MenuService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

/**
 * Модульные тесты для {@link RestaurantController}
 */
@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private MenuService menuService;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private RestaurantController restaurantController;

    /**
     * Тестирование успешного создания новой позиции меню с валидными данными
     */
    @Test
    void createMenuItem_WithValidRequest_ShouldReturnCreatedMenuItem() {
        User restaurantUser = createRestaurantUser(1L);
        CreateMenuItemRequestDto createRequest = new CreateMenuItemRequestDto(
                "Новая пицца",
                "Описание новой пиццы",
                new BigDecimal("500.00")
        );

        MenuItem savedMenuItem = createMenuItem(1L, createRequest.title(), createRequest.description(), createRequest.price(), restaurantUser);

        MenuItemResponseDto expectedResponse = new MenuItemResponseDto(
                1L,
                createRequest.title(),
                createRequest.description(),
                createRequest.price(),
                restaurantUser.getId()
        );

        Mockito.when(menuService.createMenuItem(Mockito.any(MenuItem.class), Mockito.eq(restaurantUser)))
                .thenReturn(savedMenuItem);
        Mockito.when(menuMapper.toResponse(savedMenuItem)).thenReturn(expectedResponse);

        MenuItemResponseDto result = restaurantController.createMenuItem(createRequest, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.title(), result.title());
        Assertions.assertEquals(expectedResponse.description(), result.description());
        Assertions.assertEquals(expectedResponse.price(), result.price());
        Mockito.verify(menuService).createMenuItem(Mockito.any(MenuItem.class), Mockito.eq(restaurantUser));
        Mockito.verify(menuMapper).toResponse(savedMenuItem);
    }

    /**
     * Тестирование успешного обновления существующей позиции меню
     */
    @Test
    void updateMenuItem_WithValidRequest_ShouldReturnUpdatedMenuItem() {
        User restaurantUser = createRestaurantUser(2L);
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto updateRequest = new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );

        MenuItem existingMenuItem = createMenuItem(menuItemId, "Старая пицца", "Старое описание", new BigDecimal("450.00"), restaurantUser);

        MenuItemResponseDto expectedResponse = new MenuItemResponseDto(
                menuItemId,
                updateRequest.title(),
                updateRequest.description(),
                updateRequest.price(),
                restaurantUser.getId()
        );

        Mockito.when(menuService.getMenuItemById(menuItemId)).thenReturn(existingMenuItem);
        Mockito.when(menuService.updateMenuItem(existingMenuItem, restaurantUser)).thenReturn(existingMenuItem);
        Mockito.when(menuMapper.toResponse(existingMenuItem)).thenReturn(expectedResponse);

        MenuItemResponseDto result = restaurantController.updateMenuItem(menuItemId, updateRequest, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.title(), result.title());
        Assertions.assertEquals(expectedResponse.description(), result.description());
        Assertions.assertEquals(expectedResponse.price(), result.price());
        Mockito.verify(menuService).getMenuItemById(menuItemId);
        Mockito.verify(menuService).updateMenuItem(existingMenuItem, restaurantUser);
        Mockito.verify(menuMapper).toResponse(existingMenuItem);
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
    private MenuItem createMenuItem(Long id, String title, String description, BigDecimal price, User restaurant) {
        MenuItem item = new MenuItem(title, description, price);
        item.setId(id);
        item.setRestaurant(restaurant);
        return item;
    }
}

