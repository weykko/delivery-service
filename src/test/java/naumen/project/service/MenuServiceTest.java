package naumen.project.service;

import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.mapper.MenuMapper;
import naumen.project.repository.MenuRepository;
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
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Модульные тесты для {@link MenuService}
 */
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuService menuService;

    private final User restaurantUser = createRestaurantUser();
    private final MenuItem menuItem = createMenuItem();

    /**
     * Тестирование метода получения пунктов меню с различными параметрами фильтрации и пагинации
     */
    @Test
    void getMenuItems_WithAllParameters_ShouldReturnPagedResults() {
        Long restaurantId = 1L;
        String title = "Пицца";
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(menuItem));

        Mockito.when(menuRepository.findByRestaurantIdAndTitle(restaurantId, title, pageable))
                .thenReturn(menuPage);

        Page<MenuItem> result = menuService.getMenuItems(restaurantId, title, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(menuItem, result.getContent().getFirst());
        Mockito.verify(menuRepository).findByRestaurantIdAndTitle(restaurantId, title, pageable);
    }

    /**
     * Тестирование метода получения пунктов меню с null параметрами (без фильтрации)
     */
    @Test
    void getMenuItems_WithNullParameters_ShouldReturnAllResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(menuItem));

        Mockito.when(menuRepository.findByRestaurantIdAndTitle(null, null, pageable))
                .thenReturn(menuPage);

        Page<MenuItem> result = menuService.getMenuItems(null, null, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(menuRepository).findByRestaurantIdAndTitle(null, null, pageable);
    }

    /**
     * Тестирование метода создания нового пункта меню
     */
    @Test
    void createMenuItem_WithValidRequest_ShouldCreateAndReturnMenuItem() {
        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setTitle("Новая пицца");
        newMenuItem.setDescription("Описание новой пиццы");
        newMenuItem.setPrice(new BigDecimal("500.00"));

        Mockito.when(menuRepository.save(newMenuItem)).thenReturn(newMenuItem);

        MenuItem result = menuService.createMenuItem(newMenuItem, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(restaurantUser, result.getRestaurant());
        Mockito.verify(menuRepository).save(newMenuItem);
    }

    /**
     * Тестирование метода обновления пункта меню с различными сценариями
     */
    @Test
    void updateMenuItem_WithValidOwner_ShouldUpdateAndReturnMenuItem() {
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto request = new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        Mockito.when(menuRepository.save(menuItem)).thenReturn(menuItem);

        MenuItem result = menuService.updateMenuItem(menuItemId, request, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuItem, result);
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuMapper).updateEntityFromRequest(request, menuItem);
        Mockito.verify(menuRepository).save(menuItem);
    }

    /**
     * Тестирование метода обновления пункта меню при отсутствии пункта
     */
    @Test
    void updateMenuItem_WithNotFoundMenuItem_ShouldThrowException() {
        Long menuItemId = 999L;
        UpdateMenuItemRequestDto request = new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> menuService.updateMenuItem(menuItemId, request, restaurantUser));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не найдена"));
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuMapper, Mockito.never()).updateEntityFromRequest(Mockito.any(), Mockito.any());
        Mockito.verify(menuRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование метода обновления пункта меню при попытке обновления чужого пункта
     */
    @Test
    void updateMenuItem_WithDifferentOwner_ShouldThrowForbiddenException() {
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto request = new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );
        User differentUser = createDifferentUser();

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> menuService.updateMenuItem(menuItemId, request, differentUser));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuMapper, Mockito.never()).updateEntityFromRequest(Mockito.any(), Mockito.any());
        Mockito.verify(menuRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню с различными сценариями
     */
    @Test
    void deleteMenuItem_WithValidOwner_ShouldDeleteMenuItem() {
        Long menuItemId = 1L;
        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        menuService.deleteMenuItem(menuItemId, restaurantUser);

        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository).delete(menuItem);
    }

    /**
     * Тестирование метода удаления пункта меню при отсутствии пункта
     */
    @Test
    void deleteMenuItem_WithNotFoundMenuItem_ShouldThrowException() {
        Long menuItemId = 999L;
        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> menuService.deleteMenuItem(menuItemId, restaurantUser));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню при попытке удаления чужого пункта
     */
    @Test
    void deleteMenuItem_WithDifferentOwner_ShouldThrowForbiddenException() {
        Long menuItemId = 1L;
        User differentUser = createDifferentUser();
        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> menuService.deleteMenuItem(menuItemId, differentUser));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя-ресторана
     */
    private User createRestaurantUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("restaurant@example.com");
        user.setName("Test Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    /**
     * Создание другого тестового пользователя-ресторана
     */
    private User createDifferentUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("other@example.com");
        user.setName("Other Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    /**
     * Создание тестового пункта меню
     */
    private MenuItem createMenuItem() {
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setTitle("Тестовая пицца");
        item.setDescription("Описание тестовой пиццы");
        item.setPrice(new BigDecimal("450.00"));
        item.setRestaurant(restaurantUser);
        return item;
    }
}

