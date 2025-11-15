package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Логика работы с пунктами меню в заказе
 *
 * @see OrderItem
 */
@Service
public class OrderItemService {

    private final MenuService menuService;

    public OrderItemService(
            MenuService menuService
    ) {
        this.menuService = menuService;
    }

    /**
     * Создать пункт меню, без сохранения. Происходит расчет цены.
     *
     * @param menuItemId id пункта меню
     * @param quantity   количество для заказа
     * @return объект {@link OrderItem}. Ещё не сохраненный.
     */
    public OrderItem buildOrderItem(Long menuItemId, Integer quantity) {
        MenuItem menuItem = menuService.getMenuItemById(menuItemId);

        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setItemPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }
}
