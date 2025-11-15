package naumen.project.service.order;

import naumen.project.entity.MenuItem;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;

import java.math.BigDecimal;

/**
 * Базовый класс для тестов заказов с общими вспомогательными методами
 */
public abstract class OrderTestBase {

    /**
     * Создание тестового клиента
     */
    protected User createTestClient() {
        User user = new User();
        user.setId(1L);
        user.setEmail("client@example.com");
        user.setName("Test Client");
        user.setPhone("+79991234567");
        user.setRole(Role.CLIENT);
        return user;
    }

    /**
     * Создание другого тестового клиента
     */
    protected User createDifferentClient() {
        User user = new User();
        user.setId(2L);
        user.setEmail("other-client@example.com");
        user.setName("Other Client");
        user.setPhone("+79991234568");
        user.setRole(Role.CLIENT);
        return user;
    }

    /**
     * Создание тестового ресторана
     */
    protected User createTestRestaurant() {
        User user = new User();
        user.setId(3L);
        user.setEmail("restaurant@example.com");
        user.setName("Test Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    /**
     * Создание другого тестового ресторана
     */
    protected User createDifferentRestaurant() {
        User user = new User();
        user.setId(4L);
        user.setEmail("other-restaurant@example.com");
        user.setName("Other Restaurant");
        user.setRole(Role.RESTAURANT);
        return user;
    }

    /**
     * Создание тестового курьера
     */
    protected User createTestCourier() {
        User user = new User();
        user.setId(5L);
        user.setEmail("courier@example.com");
        user.setName("Test Courier");
        user.setRole(Role.COURIER);
        return user;
    }

    /**
     * Создание другого тестового курьера
     */
    protected User createDifferentCourier() {
        User user = new User();
        user.setId(6L);
        user.setEmail("other-courier@example.com");
        user.setName("Other Courier");
        user.setRole(Role.COURIER);
        return user;
    }

    /**
     * Создание тестового заказа
     */
    protected Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(new BigDecimal("500.00"));
        order.setDeliveryAddress("Ул Пушкина");
        order.setClient(createTestClient());
        order.setRestaurant(createTestRestaurant());
        return order;
    }

    /**
     * Создание позиции заказа
     */
    protected OrderItem createOrderItem(User restaurant) {
        return createOrderItem(restaurant, new BigDecimal("250.00"));
    }

    /**
     * Создание позиции заказа с указанной ценой
     */
    protected OrderItem createOrderItem(User restaurant, BigDecimal price) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setTitle("Пицца");
        menuItem.setPrice(price);
        menuItem.setRestaurant(restaurant);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);
        orderItem.setItemPrice(price);
        orderItem.setMenuItem(menuItem);
        return orderItem;
    }
}
