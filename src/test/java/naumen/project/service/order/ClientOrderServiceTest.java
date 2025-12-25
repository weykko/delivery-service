package naumen.project.service.order;

import naumen.project.entity.MenuItem;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
import naumen.project.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Модульные тесты для {@link ClientOrderService}
 */
@ExtendWith(MockitoExtension.class)
class ClientOrderServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ClientOrderService clientOrderService;

    private final User testClient = createTestClient();
    private final User differentTestClient = createDifferentClient();
    private final User testRestaurant = createTestRestaurant();
    private final Order testOrder = createTestOrder(testRestaurant, testClient);
    private final OrderItem testOrderItem = createOrderItem(testRestaurant);

    /**
     * Тестирование успешного оформления заказа клиентом
     */
    @Test
    void createOrderWithValidItemsShouldCreateOrder() {
        List<OrderItem> orderItems = List.of(testOrderItem);
        String deliveryAddress = "Ул Пушкина";

        Mockito.when(userService.getUserById(testRestaurant.getId())).thenReturn(Optional.of(testRestaurant));
        Mockito.when(orderService.save(Mockito.any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = clientOrderService.createOrder(testRestaurant.getId(), orderItems, deliveryAddress, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatus.CREATED, result.getStatus());
        Assertions.assertEquals(deliveryAddress, result.getDeliveryAddress());
        Assertions.assertEquals(testClient, result.getClient());
        Assertions.assertEquals(testRestaurant, result.getRestaurant());
        Assertions.assertEquals(new BigDecimal("250.00"), result.getTotalPrice());
        Mockito.verify(userService).getUserById(testRestaurant.getId());
        Mockito.verify(orderService).save(Mockito.any(Order.class));
    }

    /**
     * Тестирование оформления заказа с позициями из разных ресторанов
     */
    @Test
    void createOrderWithItemsFromDifferentRestaurantsShouldThrowException() {
        List<OrderItem> orderItems = List.of(testOrderItem);
        String deliveryAddress = "Ул Пушкина";

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> clientOrderService.createOrder(testClient.getId(), orderItems, deliveryAddress, testClient));

        Assertions.assertEquals("Все позиции заказа должны принадлежать ресторану с id '1'", exception.getMessage());
        Mockito.verify(userService, Mockito.never()).getUserById(Mockito.any());
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование получения заказов клиента
     */
    @Test
    void getOrdersWithValidClientShouldReturnOrdersList() {
        List<Order> orders = List.of(testOrder);

        Mockito.when(orderService.getByClient(testClient)).thenReturn(orders);

        List<Order> result = clientOrderService.getOrders(testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testOrder, result.getFirst());
        Mockito.verify(orderService).getByClient(testClient);
    }

    /**
     * Тестирование успешного получения заказа клиента
     */
    @Test
    void getOrderWithValidClientAndOrderShouldReturnOrder() {
        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        Order result = clientOrderService.getOrder(testOrder.getId(), testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderService).getById(testOrder.getId());
    }

    /**
     * Тестирование получения заказа чужим клиентом
     */
    @Test
    void getOrderWithDifferentClientShouldThrowException() {
        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> clientOrderService.getOrder(testOrder.getId(), differentTestClient));

        Assertions.assertEquals("Заказ c id '1' не принадлежит вам", exception.getMessage());
        Mockito.verify(orderService).getById(testOrder.getId());
    }

    /**
     * Тестирование удаления заказа со статусом ACCEPTED
     */
    @Test
    void deleteOrderWithAcceptedStatusShouldThrowException() {
        testOrder.setStatus(OrderStatus.ACCEPTED);
        testOrder.setCourier(null);

        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> clientOrderService.deleteOrder(testOrder.getId(), testClient));

        Assertions.assertEquals("Заказ с id '1' уже принят в работу", exception.getMessage());
        Mockito.verify(orderService).getById(testOrder.getId());
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование удаления заказа чужим клиентом
     */
    @Test
    void deleteOrderWithDifferentClientShouldThrowException() {
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setCourier(null);

        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> clientOrderService.deleteOrder(testOrder.getId(), differentTestClient));

        Assertions.assertEquals("Заказ c id '1' не принадлежит вам", exception.getMessage());
        Mockito.verify(orderService).getById(testOrder.getId());
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового клиента
     */
    private User createTestClient() {
        User user = new User(
                "client@example.com",
                "Test Client",
                "+79991234567",
                Role.CLIENT,
                "Пушкина 17"
        );
        user.setId(1L);
        return user;
    }

    /**
     * Создание другого тестового клиента
     */
    private User createDifferentClient() {
        User user = new User(
                "other-client@example.com",
                "Other Client",
                "+79991234568",
                Role.CLIENT,
                "Ленина 10"
        );
        user.setId(2L);
        return user;
    }

    /**
     * Создание тестового ресторана
     */
    private User createTestRestaurant() {
        User user = new User(
                "restaurant@example.com",
                "Test Restaurant",
                "+79991234577",
                Role.RESTAURANT,
                "Пушкина 17"
        );
        user.setId(3L);
        return user;
    }

    /**
     * Создание позиции заказа
     */
    private OrderItem createOrderItem(User restaurant) {
        return new OrderItem(
                new MenuItem("Пицца", null, new BigDecimal("250.00"), restaurant),
                new BigDecimal("250.00"),
                1
        );
    }

    /**
     * Создает тестовый заказ
     */
    private Order createTestOrder(User restaurant, User client) {
        Order order = new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                List.of(),
                new BigDecimal("500.00"),
                restaurant,
                client
        );
        order.setId(1L);
        return order;
    }

}
