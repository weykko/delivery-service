package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
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

/**
 * Модульные тесты для {@link OrderClientService}
 */
@ExtendWith(MockitoExtension.class)
class OrderClientServiceTest extends OrderTestBase {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderClientService orderClientService;

    private final User testClient = createTestClient();
    private final User testRestaurant = createTestRestaurant();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование успешного оформления заказа клиентом
     */
    @Test
    void createOrderWithValidItemsShouldCreateOrder() {
        Long restaurantId = testRestaurant.getId();
        List<OrderItem> orderItems = List.of(createOrderItem(testRestaurant));
        String deliveryAddress = "Ул Пушкина";

        Mockito.when(userService.getUserById(restaurantId)).thenReturn(testRestaurant);
        Mockito.when(orderService.save(Mockito.any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderClientService.createOrder(restaurantId, orderItems, deliveryAddress, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatus.CREATED, result.getStatus());
        Assertions.assertEquals(deliveryAddress, result.getDeliveryAddress());
        Assertions.assertEquals(testClient, result.getClient());
        Assertions.assertEquals(testRestaurant, result.getRestaurant());
        Assertions.assertEquals(new BigDecimal("250.00"), result.getTotalPrice());
        Mockito.verify(userService).getUserById(restaurantId);
        Mockito.verify(orderService).save(Mockito.any(Order.class));
    }

    /**
     * Тестирование оформления заказа с позициями из разных ресторанов
     */
    @Test
    void createOrderWithItemsFromDifferentRestaurantsShouldThrowException() {
        Long restaurantId = 1L;
        User differentRestaurant = createDifferentRestaurant();
        List<OrderItem> orderItems = List.of(createOrderItem(differentRestaurant));
        String deliveryAddress = "Ул Пушкина";

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> orderClientService.createOrder(restaurantId, orderItems, deliveryAddress, testClient));

        Assertions.assertTrue(exception.getMessage().contains("Все позиции заказа должны принадлежать ресторану"));
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

        List<Order> result = orderClientService.getOrders(testClient);

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
        Long orderId = 1L;

        Mockito.when(orderService.getById(orderId)).thenReturn(testOrder);

        Order result = orderClientService.getOrder(orderId, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование получения заказа чужим клиентом
     */
    @Test
    void getOrderWithDifferentClientShouldThrowException() {
        Long orderId = 1L;
        User differentClient = createDifferentClient();

        Mockito.when(orderService.getById(orderId)).thenReturn(testOrder);

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> orderClientService.getOrder(orderId, differentClient));

        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вам"));
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование удаления заказа со статусом ACCEPTED
     */
    @Test
    void deleteOrderWithAcceptedStatusShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);
        order.setCourier(null);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> orderClientService.deleteOrder(orderId, testClient));

        Assertions.assertTrue(exception.getMessage().contains("уже принят в работу"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование удаления заказа чужим клиентом
     */
    @Test
    void deleteOrderWithDifferentClientShouldThrowException() {
        Long orderId = 1L;
        User differentClient = createDifferentClient();
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);
        order.setCourier(null);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> orderClientService.deleteOrder(orderId, differentClient));

        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вам"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }
}
