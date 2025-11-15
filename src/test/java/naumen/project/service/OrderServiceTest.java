package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.repository.OrderRepository;
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
 * Модульные тесты для {@link OrderService}
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private final User testClient = createTestClient();
    private final User testRestaurant = createTestRestaurant();
    private final User testCourier = createTestCourier();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование получения доступных заказов для курьеров
     */
    @Test
    void getAvailableOrdersForCourier_WithValidPageable_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));

        Mockito.when(orderRepository.findAvailableOrdersForCourier(pageable)).thenReturn(ordersPage);

        Page<Order> result = orderService.getAvailableOrdersForCourier(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(testOrder, result.getContent().getFirst());
        Mockito.verify(orderRepository).findAvailableOrdersForCourier(pageable);
    }

    /**
     * Тестирование получения активных заказов курьера
     */
    @Test
    void getActiveOrdersByCourier_WithValidCourier_ShouldReturnOrdersList() {
        List<Order> orders = List.of(testOrder);

        Mockito.when(orderRepository.findActiveOrdersByCourier(testCourier)).thenReturn(orders);

        List<Order> result = orderService.getActiveOrdersByCourier(testCourier);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testOrder, result.getFirst());
        Mockito.verify(orderRepository).findActiveOrdersByCourier(testCourier);
    }

    /**
     * Тестирование успешного приема заказа курьером
     */
    @Test
    void acceptOrderByCourier_WithValidOrder_ShouldAssignCourier() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(null);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.acceptOrderByCourier(orderId, testCourier);

        Assertions.assertEquals(testCourier, order.getCourier());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование приема заказа курьером, когда заказ уже принят другим курьером
     */
    @Test
    void acceptOrderByCourier_WithAlreadyAcceptedOrder_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.acceptOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже принят курьером"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа курьером со статусом PREPARED
     */
    @Test
    void pickUpOrderByCourier_WithPreparedStatus_ShouldChangeToDelivering() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.pickUpOrderByCourier(orderId, testCourier);

        Assertions.assertEquals(OrderStatus.DELIVERING, order.getStatus());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование забора заказа курьером со статусом DELIVERING
     */
    @Test
    void pickUpOrderByCourier_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.pickUpOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже доставляется"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа курьером со статусом COMPLETED
     */
    @Test
    void pickUpOrderByCourier_WithCompletedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.pickUpOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже доставлен"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа курьером с неподходящим статусом
     */
    @Test
    void pickUpOrderByCourier_WithCreatedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.pickUpOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("ещё не готов"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа чужим курьером
     */
    @Test
    void pickUpOrderByCourier_WithDifferentCourier_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.PREPARED);
        User differentCourier = createDifferentCourier();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.pickUpOrderByCourier(orderId, differentCourier));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вам"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование доставки заказа курьером со статусом DELIVERING
     */
    @Test
    void deliverOrderByCourier_WithDeliveringStatus_ShouldChangeToCompleted() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.deliverOrderByCourier(orderId, testCourier);

        Assertions.assertEquals(OrderStatus.COMPLETED, order.getStatus());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование доставки заказа курьером со статусом COMPLETED
     */
    @Test
    void deliverOrderByCourier_WithCompletedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.deliverOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже доставлен"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование доставки заказа курьером с неподходящим статусом
     */
    @Test
    void deliverOrderByCourier_WithCreatedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.deliverOrderByCourier(orderId, testCourier));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("ещё не доставляется"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование получения активных заказов ресторана
     */
    @Test
    void getActiveOrdersByRestaurant_WithValidRestaurant_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));

        Mockito.when(orderRepository.findActiveOrdersByRestaurant(testRestaurant, pageable))
                .thenReturn(ordersPage);

        Page<Order> result = orderService.getActiveOrdersByRestaurant(testRestaurant, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(testOrder, result.getContent().getFirst());
        Mockito.verify(orderRepository).findActiveOrdersByRestaurant(testRestaurant, pageable);
    }

    /**
     * Тестирование получения заказа ресторана по идентификатору
     */
    @Test
    void getOrderByRestaurant_WithValidOrder_ShouldReturnOrder() {
        Long orderId = 1L;

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderByRestaurant(orderId, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderRepository).findById(orderId);
    }

    /**
     * Тестирование получения заказа ресторана с неверным рестораном
     */
    @Test
    void getOrderByRestaurant_WithDifferentRestaurant_ShouldThrowException() {
        Long orderId = 1L;
        User differentRestaurant = createDifferentRestaurant();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.getOrderByRestaurant(orderId, differentRestaurant));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        Mockito.verify(orderRepository).findById(orderId);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом CREATED
     */
    @Test
    void prepareOrderByRestaurant_WithCreatedStatus_ShouldChangeToAccepted() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.prepareOrderByRestaurant(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом ACCEPTED
     */
    @Test
    void prepareOrderByRestaurant_WithAcceptedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.prepareOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже готовиться"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом PREPARED
     */
    @Test
    void prepareOrderByRestaurant_WithPreparedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.prepareOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже приготовлен"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом DELIVERING
     */
    @Test
    void prepareOrderByRestaurant_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.prepareOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже был отдан курьеру"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом ACCEPTED
     */
    @Test
    void readyOrderByRestaurant_WithAcceptedStatus_ShouldChangeToPrepared() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.readyOrderByRestaurant(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.PREPARED, order.getStatus());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование пометки заказа как готового со статусом CREATED
     */
    @Test
    void readyOrderByRestaurant_WithCreatedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.readyOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("еще не начал готовиться"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом PREPARED
     */
    @Test
    void readyOrderByRestaurant_WithPreparedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.readyOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже приготовлен"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом DELIVERING
     */
    @Test
    void readyOrderByRestaurant_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.readyOrderByRestaurant(orderId, testRestaurant));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже был отдан курьеру"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование успешного оформления заказа клиентом
     */
    @Test
    void createOrderByClient_WithValidItems_ShouldCreateOrder() {
        Long restaurantId = testRestaurant.getId();
        List<OrderItem> orderItems = List.of(createOrderItem(testRestaurant));
        String deliveryAddress = "Ул Пушкина";

        Mockito.when(userService.getById(restaurantId)).thenReturn(testRestaurant);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrderByClient(restaurantId, orderItems, deliveryAddress, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatus.CREATED, result.getStatus());
        Assertions.assertEquals(deliveryAddress, result.getDeliveryAddress());
        Assertions.assertEquals(testClient, result.getClient());
        Assertions.assertEquals(testRestaurant, result.getRestaurant());
        Assertions.assertEquals(new BigDecimal("250.00"), result.getTotalPrice());
        Mockito.verify(userService).getById(restaurantId);
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
    }

    /**
     * Тестирование оформления заказа с позициями из разных ресторанов
     */
    @Test
    void createOrderByClient_WithItemsFromDifferentRestaurants_ShouldThrowException() {
        Long restaurantId = 1L;
        User differentRestaurant = createDifferentRestaurant();
        List<OrderItem> orderItems = List.of(createOrderItem(differentRestaurant));
        String deliveryAddress = "Ул Пушкина";

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.createOrderByClient(restaurantId, orderItems, deliveryAddress, testClient));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("Все позиции заказа должны принадлежать ресторану"));
        Mockito.verify(userService, Mockito.never()).getById(Mockito.any());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование получения заказов клиента
     */
    @Test
    void getOrdersByClient_WithValidClient_ShouldReturnOrdersList() {
        List<Order> orders = List.of(testOrder);

        Mockito.when(orderRepository.findOrdersByClient(testClient)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByClient(testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testOrder, result.getFirst());
        Mockito.verify(orderRepository).findOrdersByClient(testClient);
    }

    /**
     * Тестирование получения заказа клиента по идентификатору
     */
    @Test
    void getOrderByClient_WithValidOrder_ShouldReturnOrder() {
        Long orderId = 1L;

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderByClient(orderId, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderRepository).findById(orderId);
    }

    /**
     * Тестирование получения заказа чужим клиентом
     */
    @Test
    void getOrderByClient_WithDifferentClient_ShouldThrowException() {
        Long orderId = 1L;
        User differentClient = createDifferentClient();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.getOrderByClient(orderId, differentClient));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вам"));
        Mockito.verify(orderRepository).findById(orderId);
    }

    /**
     * Тестирование удаления заказа со статусом CREATED
     */
    @Test
    void deleteOrderByClient_WithCreatedStatusAndNoCourier_ShouldChangeToDeleted() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);
        order.setCourier(null);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        orderService.deleteOrderByClient(orderId, testClient);

        Assertions.assertEquals(OrderStatus.DELETED, order.getStatus());
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository).save(order);
    }

    /**
     * Тестирование удаления заказа со статусом CREATED, но с назначенным курьером
     */
    @Test
    void deleteOrderByClient_WithCreatedStatusAndCourier_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);
        order.setCourier(testCourier);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.deleteOrderByClient(orderId, testClient));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже принят в работу"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование удаления заказа со статусом ACCEPTED
     */
    @Test
    void deleteOrderByClient_WithAcceptedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);
        order.setCourier(null);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.deleteOrderByClient(orderId, testClient));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("уже принят в работу"));
        Mockito.verify(orderRepository).findById(orderId);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование получения несуществующего заказа
     */
    @Test
    void getOrderById_WithNonExistentOrder_ShouldThrowException() {
        Long orderId = 999L;

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> orderService.getOrderByClient(orderId, testClient));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertTrue(exception.getMessage().contains("не найден"));
        Mockito.verify(orderRepository).findById(orderId);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового клиента
     */
    private User createTestClient() {
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
    private User createDifferentClient() {
        User user = new User();
        user.setId(2L);
        user.setEmail("other-client@example.com");
        user.setName("Other Client");
        user.setPhone("+79997654321");
        user.setRole(Role.CLIENT);
        return user;
    }

    /**
     * Создание тестового ресторана
     */
    private User createTestRestaurant() {
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
    private User createDifferentRestaurant() {
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
    private User createTestCourier() {
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
    private User createDifferentCourier() {
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
    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(new BigDecimal("500.00"));
        order.setDeliveryAddress("Ул Пушкина");
        order.setClient(testClient);
        order.setRestaurant(testRestaurant);
        return order;
    }

    /**
     * Создание тестового элемента заказа
     */
    private OrderItem createOrderItem(User restaurant) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setTitle("Пицца");
        menuItem.setPrice(new BigDecimal("250.00"));
        menuItem.setRestaurant(restaurant);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(1);
        orderItem.setItemPrice(new BigDecimal("250.00"));
        orderItem.setMenuItem(menuItem);
        return orderItem;
    }
}
