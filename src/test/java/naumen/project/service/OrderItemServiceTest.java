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
    void buildOrderItem_ShouldCreateOrderItemWithCalculatedPrice() {
        Long menuItemId = 1L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal expectedTotalPrice = new BigDecimal("200.00");

        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemId);
        menuItem.setPrice(price);

        Mockito.when(menuService.getMenuItemById(menuItemId)).thenReturn(menuItem);

        OrderItem result = orderItemService.buildOrderItem(menuItemId, quantity);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(menuItem, result.getMenuItem());
        Assertions.assertEquals(quantity, result.getQuantity());
        Assertions.assertEquals(expectedTotalPrice, result.getItemPrice());
        Mockito.verify(menuService).getMenuItemById(menuItemId);
    }
}

