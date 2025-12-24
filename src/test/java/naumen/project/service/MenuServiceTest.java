package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
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

    private final User testRestaurant = createTestRestaurantUser(1L);
    private final User differentTestUser = createTestRestaurantUser(2L);
    private final MenuItem testMenuItem = createTestMenuItem(testRestaurant);

    /**
     * Тестирование метода получения пунктов меню с различными параметрами фильтрации и пагинации
     */
    @Test
    void getMenuItemsWithAllParametersShouldReturnPagedResults() {
        Long restaurantId = 1L;
        String title = "Пицца";
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(testMenuItem));

        Mockito.when(menuRepository.findByRestaurantIdAndTitle(restaurantId, title, pageable))
                .thenReturn(menuPage);

        Page<MenuItem> result = menuService.getMenuItems(restaurantId, title, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(testMenuItem, result.getContent().getFirst());
        Mockito.verify(menuRepository).findByRestaurantIdAndTitle(restaurantId, title, pageable);
    }

    /**
     * Тестирование метода получения пунктов меню с null параметрами (без фильтрации)
     */
    @Test
    void getMenuItemsWithNullParametersShouldReturnAllResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(testMenuItem));

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
    void createMenuItemWithValidRequestShouldSave() {
        Mockito.when(menuRepository.save(testMenuItem)).thenReturn(testMenuItem);

        MenuItem result = menuService.save(testMenuItem);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testRestaurant, result.getRestaurant());
        Mockito.verify(menuRepository).save(testMenuItem);
    }

    /**
     * Тестирование метода обновления пункта меню с валидным владельцем
     */
    @Test
    void updateMenuItemWithValidOwnerShouldUpdateAndReturnMenuItem() {
        Mockito.when(menuRepository.save(testMenuItem)).thenReturn(testMenuItem);

        MenuItem result = menuService.updateMenuItem(testMenuItem, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testMenuItem, result);
        Mockito.verify(menuRepository).save(testMenuItem);
    }

    /**
     * Тестирование метода обновления пункта меню при попытке обновления чужого пункта
     */
    @Test
    void updateMenuItemWithDifferentOwnerShouldThrowForbiddenException() {
        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> menuService.updateMenuItem(testMenuItem, differentTestUser));

        Assertions.assertEquals("Позиция меню с id '1' не принадлежит вашему ресторану",
                exception.getMessage());
        Mockito.verify(menuRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню с валидным владельцем
     */
    @Test
    void deleteMenuItemWithValidOwnerShouldDeleteMenuItem() {
        Mockito.when(menuRepository.findById(testMenuItem.getId())).thenReturn(Optional.of(testMenuItem));

        menuService.deleteMenuItem(testMenuItem.getId(), testRestaurant);

        Mockito.verify(menuRepository).findById(testMenuItem.getId());
        Mockito.verify(menuRepository).delete(testMenuItem);
    }

    /**
     * Тестирование метода удаления пункта меню при отсутствии пункта
     */
    @Test
    void deleteMenuItemWithNotFoundMenuItemShouldThrowException() {
        Long menuItemId = 999L;

        Mockito.when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> menuService.deleteMenuItem(menuItemId, testRestaurant));

        Assertions.assertEquals("Не удалось удалить, причина: Позиция меню с id '999' не найдена",
                exception.getMessage());
        Mockito.verify(menuRepository).findById(menuItemId);
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Тестирование метода удаления пункта меню при попытке удаления чужого пункта
     */
    @Test
    void deleteMenuItemWithDifferentOwnerShouldThrowForbiddenException() {
        Mockito.when(menuRepository.findById(testMenuItem.getId())).thenReturn(Optional.of(testMenuItem));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> menuService.deleteMenuItem(testMenuItem.getId(), differentTestUser));

        Assertions.assertEquals("Позиция меню с id '1' не принадлежит вашему ресторану",
                exception.getMessage());
        Mockito.verify(menuRepository).findById(testMenuItem.getId());
        Mockito.verify(menuRepository, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Тестирование получения позиции меню по ID
     */
    @Test
    void getMenuItemByIdWithExistingIdShouldReturnMenuItem() {
        Mockito.when(menuRepository.findById(testMenuItem.getId())).thenReturn(Optional.of(testMenuItem));

        MenuItem result = menuService.getMenuItemById(testMenuItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Не должно быть равно null"));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testMenuItem, result);
        Mockito.verify(menuRepository).findById(testMenuItem.getId());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя-ресторана
     */
    private User createTestRestaurantUser(Long id) {
        User user = new User("restaurant@example.com", "Test Restaurant",
                "+79991234567", Role.RESTAURANT, "Пушкина 17");
        user.setId(id);
        return user;
    }

    /**
     * Создание тестового пункта меню
     */
    private MenuItem createTestMenuItem(User restaurant) {
        MenuItem item = new MenuItem("Pizza", "description", new BigDecimal(450), restaurant);
        item.setId(1L);
        return item;
    }
}

