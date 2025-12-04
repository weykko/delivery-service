package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.PermissionCheckFailedException;
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

    @InjectMocks
    private MenuService menuService;

    /**
     * Тестирование метода получения пунктов меню с различными параметрами фильтрации и пагинации
     */
    @Test
    void getMenuItemsWithAllParametersShouldReturnPagedResults() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);
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
    void getMenuItemsWithNullParametersShouldReturnAllResults() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);
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
    void createMenuItemWithValidRequestShouldCreateAndReturnMenuItem() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem newMenuItem = createMenuItem();

        Mockito.when(menuRepository.save(newMenuItem)).thenReturn(newMenuItem);

        MenuItem result = menuService.createMenuItem(newMenuItem, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(restaurantUser, result.getRestaurant());
        Mockito.verify(menuRepository).save(newMenuItem);
    }

    /**
     * Тестирование метода обновления пункта меню с валидным владельцем
     */
    @Test
    void updateMenuItemWithValidOwnerShouldUpdateAndReturnMenuItem() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);

        Mockito.when(menuRepository.save(menuItem)).thenReturn(menuItem);

        MenuItem result = menuService.updateMenuItem(menuItem, restaurantUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuItem, result);
        Mockito.verify(menuRepository).save(menuItem);
    }

    /**
     * Тестирование метода обновления пункта меню при попытке обновления чужого пункта
     */
    @Test
    void updateMenuItemWithDifferentOwnerShouldThrowForbiddenException() {
        User restaurantUser = createRestaurantUser(1L);
        User differentUser = createRestaurantUser(2L);
        MenuItem menuItem = createMenuItem(restaurantUser);

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> menuService.updateMenuItem(menuItem, differentUser));

        Assertions.assertEquals("Позиция меню с id '1' не принадлежит вашему ресторану",
                exception.getMessage());
        Mockito.verify(menuRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню с валидным владельцем
     */
    @Test
    void deleteMenuItemWithValidOwnerShouldDeleteMenuItem() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);
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
    void deleteMenuItemWithNotFoundMenuItemShouldThrowException() {
        User restaurantUser = createRestaurantUser(1L);
        Long menuItemId = 999L;

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> menuService.deleteMenuItem(menuItemId, restaurantUser));

        Assertions.assertEquals("Позиция меню с id '999' не найдена", exception.getMessage());
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню при попытке удаления чужого пункта
     */
    @Test
    void deleteMenuItemWithDifferentOwnerShouldThrowForbiddenException() {
        User restaurantUser = createRestaurantUser(1L);
        User differentUser = createRestaurantUser(2L);
        MenuItem menuItem = createMenuItem(restaurantUser);
        Long menuItemId = 1L;

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> menuService.deleteMenuItem(menuItemId, differentUser));

        Assertions.assertEquals("Позиция меню с id '1' не принадлежит вашему ресторану",
                exception.getMessage());
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Тестирование получения позиции меню по ID
     */
    @Test
    void getMenuItemByIdWithExistingIdShouldReturnMenuItem() {
        User restaurantUser = createRestaurantUser(1L);
        MenuItem menuItem = createMenuItem(restaurantUser);
        Long menuItemId = 1L;

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        MenuItem result = menuService.getMenuItemById(menuItemId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuItem, result);
        Mockito.verify(menuRepository).findById(menuItemId);
    }

    /**
     * Тестирование получения позиции меню по несуществующему ID
     */
    @Test
    void getMenuItemByIdWithNonExistingIdShouldThrowException() {
        Long menuItemId = 999L;

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> menuService.getMenuItemById(menuItemId));

        Assertions.assertEquals("Позиция меню с id '999' не найдена", exception.getMessage());
        Mockito.verify(menuRepository).findById(menuItemId);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя-ресторана
     */
    private User createRestaurantUser(Long id) {
        User user = new User("restaurant@example.com", "Test Restaurant", "+79991234567", Role.RESTAURANT);
        user.setId(id);
        return user;
    }

    /**
     * Создание тестового пункта меню
     */
    private MenuItem createMenuItem(User restaurant) {
        MenuItem item = new MenuItem("Pizza", "description", new BigDecimal(450));
        item.setId(1L);
        item.setRestaurant(restaurant);
        return item;
    }

    /**
     * Создание новго тестового пункта меню
     */
    private MenuItem createMenuItem() {
        MenuItem item = new MenuItem("Pizza", "description", new BigDecimal(450));
        item.setId(1L);
        return item;
    }
}

