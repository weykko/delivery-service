package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.OrderItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Модульные тесты для {@link OrderItemService}
 */
@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private OrderItemService orderItemService;

    /**
     * Тестирование создания пункта заказа с расчетом цены
     */
    @Test
    void buildOrderItemShouldCreateOrderItemWithCalculatedPrice() {
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal expectedTotalPrice = new BigDecimal("200.00");

        MenuItem menuItem = new MenuItem("bread", "Delicious bread", price, null);
        menuItem.setId(menuItemId);

        Mockito.when(menuService.getMenuItemById(menuItemId)).thenReturn(Optional.of(menuItem));

        OrderItem result = orderItemService.buildOrderItem(menuItemId, quantity);

        Assertions.assertEquals(expectedTotalPrice, result.getItemPrice());
        Mockito.verify(menuService).getMenuItemById(menuItemId);
    }
}

