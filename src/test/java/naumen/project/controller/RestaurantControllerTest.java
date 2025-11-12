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

    private final User restaurantUser = createRestaurantUser();

    /**
     * Тестирование успешного создания новой позиции меню с валидными данными
     */
    @Test
    void createMenuItem_WithValidRequest_ShouldReturnCreatedMenuItem() {
        CreateMenuItemRequestDto createRequest = new CreateMenuItemRequestDto(
                "Новая пицца",
                "Описание новой пиццы",
                new BigDecimal("500.00")
        );

        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setTitle(createRequest.title());
        newMenuItem.setDescription(createRequest.description());
        newMenuItem.setPrice(createRequest.price());

        MenuItem savedMenuItem = new MenuItem();
        savedMenuItem.setId(1L);
        savedMenuItem.setTitle(createRequest.title());
        savedMenuItem.setDescription(createRequest.description());
        savedMenuItem.setPrice(createRequest.price());
        savedMenuItem.setRestaurant(restaurantUser);

        MenuItemResponseDto expectedResponse = new MenuItemResponseDto(
                1L,
                createRequest.title(),
                createRequest.description(),
                createRequest.price(),
                restaurantUser.getId()
        );

        Mockito.when(menuMapper.toEntity(createRequest)).thenReturn(newMenuItem);
        Mockito.when(menuService.createMenuItem(newMenuItem, restaurantUser)).thenReturn(savedMenuItem);
        Mockito.when(menuMapper.toResponse(savedMenuItem)).thenReturn(expectedResponse);

        MenuItemResponseDto result = restaurantController.createMenuItem(createRequest, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.title(), result.title());
        Assertions.assertEquals(expectedResponse.description(), result.description());
        Assertions.assertEquals(expectedResponse.price(), result.price());
        Mockito.verify(menuMapper).toEntity(createRequest);
        Mockito.verify(menuService).createMenuItem(newMenuItem, restaurantUser);
        Mockito.verify(menuMapper).toResponse(savedMenuItem);
    }

    /**
     * Тестирование успешного обновления существующей позиции меню
     */
    @Test
    void updateMenuItem_WithValidRequest_ShouldReturnUpdatedMenuItem() {
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto updateRequest = new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );

        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setId(menuItemId);
        updatedMenuItem.setTitle(updateRequest.title());
        updatedMenuItem.setDescription(updateRequest.description());
        updatedMenuItem.setPrice(updateRequest.price());
        updatedMenuItem.setRestaurant(restaurantUser);

        MenuItemResponseDto expectedResponse = new MenuItemResponseDto(
                menuItemId,
                updateRequest.title(),
                updateRequest.description(),
                updateRequest.price(),
                restaurantUser.getId()
        );

        Mockito.when(menuService.updateMenuItem(menuItemId, updateRequest, restaurantUser))
                .thenReturn(updatedMenuItem);
        Mockito.when(menuMapper.toResponse(updatedMenuItem)).thenReturn(expectedResponse);

        MenuItemResponseDto result = restaurantController.updateMenuItem(menuItemId, updateRequest, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.title(), result.title());
        Assertions.assertEquals(expectedResponse.description(), result.description());
        Assertions.assertEquals(expectedResponse.price(), result.price());
        Mockito.verify(menuService).updateMenuItem(menuItemId, updateRequest, restaurantUser);
        Mockito.verify(menuMapper).toResponse(updatedMenuItem);
    }

    /**
     * Тестирование успешного удаления позиции меню владельцем ресторана
     */
    @Test
    void deleteMenuItem_WithValidOwner_ShouldCallDeleteService() {
        Long menuItemId = 1L;

        restaurantController.deleteMenuItem(menuItemId, restaurantUser);

        Mockito.verify(menuService).deleteMenuItem(menuItemId, restaurantUser);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя-ресторана
     */
    private User createRestaurantUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("restaurant@example.com");
        user.setName("Test Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }
}

