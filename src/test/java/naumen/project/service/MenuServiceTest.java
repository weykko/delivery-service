package naumen.project.service;

import naumen.project.dto.menu.CreateMenuItemRequestDto;
import naumen.project.dto.menu.MenuItemResponseDto;
import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.mapper.MenuMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для {@link MenuService}
 */
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private MenuService menuService;

    private final User restaurantUser = createRestaurantUser();
    private final MenuItem menuItem = createMenuItem();
    private final MenuItemResponseDto menuItemResponseDto = createMenuItemResponseDto();

    @Test
    void getMenuItems_WithAllParameters_ShouldReturnPagedResults() {
        Long restaurantId = 1L;
        String title = "Пицца";
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(menuItem));
        Page<MenuItemResponseDto> responsePage = new PageImpl<>(List.of(menuItemResponseDto));
        PagedResponseDto<MenuItemResponseDto> expectedResponse = createPagedResponse();

        when(menuRepository.findByRestaurantIdAndTitle(restaurantId, title, pageable))
                .thenReturn(menuPage);
        when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponseDto);
        when(pageMapper.toResponse(responsePage)).thenReturn(expectedResponse);

        PagedResponseDto<MenuItemResponseDto> result = menuService.getMenuItems(restaurantId, title, pageable);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(menuRepository).findByRestaurantIdAndTitle(restaurantId, title, pageable);
        verify(menuMapper).toResponse(menuItem);
        verify(pageMapper).toResponse(responsePage);
    }

    @Test
    void getMenuItems_WithNullParameters_ShouldReturnAllResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuItem> menuPage = new PageImpl<>(List.of(menuItem));
        Page<MenuItemResponseDto> responsePage = new PageImpl<>(List.of(menuItemResponseDto));
        PagedResponseDto<MenuItemResponseDto> expectedResponse = createPagedResponse();

        when(menuRepository.findByRestaurantIdAndTitle(null, null, pageable))
                .thenReturn(menuPage);
        when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponseDto);
        when(pageMapper.toResponse(responsePage)).thenReturn(expectedResponse);

        PagedResponseDto<MenuItemResponseDto> result = menuService.getMenuItems(null, null, pageable);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void createMenuItem_WithValidRequest_ShouldCreateAndReturnMenuItem() {
        CreateMenuItemRequestDto request = createCreateMenuItemRequest();
        when(menuMapper.toEntity(request)).thenReturn(menuItem);
        when(menuRepository.save(menuItem)).thenReturn(menuItem);
        when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponseDto);

        MenuItemResponseDto result = menuService.createMenuItem(request, restaurantUser);

        assertNotNull(result);
        assertEquals(menuItemResponseDto, result);
        verify(menuMapper).toEntity(request);
        verify(menuRepository).save(menuItem);
        verify(menuMapper).toResponse(menuItem);
        assertEquals(restaurantUser, menuItem.getRestaurant());
    }

    @Test
    void updateMenuItem_WithValidOwner_ShouldUpdateAndReturnMenuItem() {
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto request = createUpdateMenuItemRequest();

        when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
        when(menuRepository.save(menuItem)).thenReturn(menuItem);
        when(menuMapper.toResponse(menuItem)).thenReturn(menuItemResponseDto);

        MenuItemResponseDto result = menuService.updateMenuItem(menuItemId, request, restaurantUser);

        assertNotNull(result);
        assertEquals(menuItemResponseDto, result);
        verify(menuRepository).findById(menuItemId);
        verify(menuMapper).updateEntityFromRequest(request, menuItem);
        verify(menuRepository).save(menuItem);
        verify(menuMapper).toResponse(menuItem);
    }

    @Test
    void updateMenuItem_WithNotFoundMenuItem_ShouldThrowException() {
        Long menuItemId = 999L;
        UpdateMenuItemRequestDto request = createUpdateMenuItemRequest();

        when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        WebException exception = assertThrows(WebException.class,
                () -> menuService.updateMenuItem(menuItemId, request, restaurantUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("не найдена"));
        verify(menuRepository).findById(menuItemId);
        verify(menuMapper, never()).updateEntityFromRequest(any(), any());
        verify(menuRepository, never()).save(any());
    }

    @Test
    void updateMenuItem_WithDifferentOwner_ShouldThrowForbiddenException() {
        Long menuItemId = 1L;
        UpdateMenuItemRequestDto request = createUpdateMenuItemRequest();
        User differentUser = createDifferentUser();

        when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        WebException exception = assertThrows(WebException.class,
                () -> menuService.updateMenuItem(menuItemId, request, differentUser));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        verify(menuRepository).findById(menuItemId);
        verify(menuMapper, never()).updateEntityFromRequest(any(), any());
        verify(menuRepository, never()).save(any());
    }

    @Test
    void deleteMenuItem_WithValidOwner_ShouldDeleteMenuItem() {
        Long menuItemId = 1L;
        when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        menuService.deleteMenuItem(menuItemId, restaurantUser);

        verify(menuRepository).findById(menuItemId);
        verify(menuRepository).delete(menuItem);
    }

    @Test
    void deleteMenuItem_WithNotFoundMenuItem_ShouldThrowException() {
        Long menuItemId = 999L;
        when(menuRepository.findById(menuItemId)).thenReturn(Optional.empty());

        WebException exception = assertThrows(WebException.class,
                () -> menuService.deleteMenuItem(menuItemId, restaurantUser));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(menuRepository).findById(menuItemId);
        verify(menuRepository, never()).delete(any());
    }

    @Test
    void deleteMenuItem_WithDifferentOwner_ShouldThrowForbiddenException() {
        Long menuItemId = 1L;
        User differentUser = createDifferentUser();
        when(menuRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

        WebException exception = assertThrows(WebException.class,
                () -> menuService.deleteMenuItem(menuItemId, differentUser));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(menuRepository).findById(menuItemId);
        verify(menuRepository, never()).delete(any());
    }

    // Вспомогательные методы для создания тестовых данных

    private User createRestaurantUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("restaurant@example.com");
        user.setName("Test Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    private User createDifferentUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("other@example.com");
        user.setName("Other Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    private MenuItem createMenuItem() {
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setTitle("Тестовая пицца");
        item.setDescription("Описание тестовой пиццы");
        item.setPrice(new BigDecimal("450.00"));
        item.setRestaurant(restaurantUser);
        return item;
    }

    private MenuItemResponseDto createMenuItemResponseDto() {
        return new MenuItemResponseDto(
                1L,
                "Тестовая пицца",
                "Описание тестовой пиццы",
                new BigDecimal("450.00"),
                1L
        );
    }

    private CreateMenuItemRequestDto createCreateMenuItemRequest() {
        return new CreateMenuItemRequestDto(
                "Новая пицца",
                "Описание новой пиццы",
                new BigDecimal("500.00")
        );
    }

    private UpdateMenuItemRequestDto createUpdateMenuItemRequest() {
        return new UpdateMenuItemRequestDto(
                "Обновленная пицца",
                "Обновленное описание",
                new BigDecimal("550.00")
        );
    }

    private PagedResponseDto<MenuItemResponseDto> createPagedResponse() {
        return new PagedResponseDto<>(
                List.of(menuItemResponseDto),
                1,
                1,
                0,
                10
        );
    }
}
