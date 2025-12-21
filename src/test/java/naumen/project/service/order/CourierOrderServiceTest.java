package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
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
 * Модульные тесты для {@link CourierOrderService}
 */
@ExtendWith(MockitoExtension.class)
class CourierOrderServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CourierOrderService courierOrderService;

    private final User testCourier = createTestCourier();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование получения доступных заказов для курьеров
     */
    @Test
    void getAvailableOrders_WithValidPageable_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));

        Mockito.when(orderService.getAvailableOrdersForCourier(pageable)).thenReturn(ordersPage);

        Page<Order> result = courierOrderService.getAvailableOrders(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(testOrder, result.getContent().getFirst());
        Mockito.verify(orderService).getAvailableOrdersForCourier(pageable);
    }

    /**
     * Тестирование получения активных заказов курьера
     */
    @Test
    void getActiveOrders_WithValidCourier_ShouldReturnOrdersList() {
        List<Order> orders = List.of(testOrder);

        Mockito.when(orderService.getActiveOrdersByCourier(testCourier)).thenReturn(orders);

        List<Order> result = courierOrderService.getActiveOrders(testCourier);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testOrder, result.getFirst());
        Mockito.verify(orderService).getActiveOrdersByCourier(testCourier);
    }

    /**
     * Тестирование успешного приема заказа курьером
     */
    @Test
    void acceptOrder_WithValidOrder_ShouldAssignCourier() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(null);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderService.save(order)).thenReturn(order);

        courierOrderService.acceptOrder(orderId, testCourier);

        Assertions.assertEquals(testCourier, order.getCourier());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(order);
    }

    /**
     * Тестирование приема заказа курьером, когда заказ уже принят другим курьером
     */
    @Test
    void acceptOrder_WithAlreadyAcceptedOrder_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> courierOrderService.acceptOrder(orderId, testCourier));

        Assertions.assertEquals("Заказ с id '1' уже принят курьером", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование принятия заказа курьером со статусом PREPARED
     */
    @Test
    void pickUpOrder_WithPreparedStatus_ShouldChangeToDelivering() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderService.save(order)).thenReturn(order);

        courierOrderService.pickUpOrder(orderId, testCourier);

        Assertions.assertEquals(OrderStatus.DELIVERING, order.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(order);
    }

    /**
     * Тестирование принятия заказа чужим курьером
     */
    @Test
    void pickUpOrder_WithDifferentCourier_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.PREPARED);
        User differentCourier = createDifferentCourier();

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> courierOrderService.pickUpOrder(orderId, differentCourier));

        Assertions.assertEquals("Заказ с id '1' не принадлежит вам", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа со статусом DELIVERING
     */
    @Test
    void pickUpOrder_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> courierOrderService.pickUpOrder(orderId, testCourier));

        Assertions.assertEquals("Заказ с id '1' уже доставляется", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа со статусом COMPLETED
     */
    @Test
    void pickUpOrder_WithCompletedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> courierOrderService.pickUpOrder(orderId, testCourier));

        Assertions.assertEquals("Заказ с id '1' уже доставлен", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование забора заказа со статусом CREATED
     */
    @Test
    void pickUpOrder_WithCreatedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> courierOrderService.pickUpOrder(orderId, testCourier));

        Assertions.assertEquals("Заказ с id '1' ещё не готов", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование доставки заказа курьером со статусом DELIVERING
     */
    @Test
    void deliverOrder_WithDeliveringStatus_ShouldChangeToCompleted() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderService.save(order)).thenReturn(order);

        courierOrderService.deliverOrder(orderId, testCourier);

        Assertions.assertEquals(OrderStatus.COMPLETED, order.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(order);
    }

    /**
     * Тестирование доставки заказа чужим курьером
     */
    @Test
    void deliverOrder_WithDifferentCourier_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.DELIVERING);
        User differentCourier = createDifferentCourier();

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> courierOrderService.deliverOrder(orderId, differentCourier));

        Assertions.assertEquals("Заказ с id '1' не принадлежит вам", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование доставки заказа со статусом COMPLETED
     */
    @Test
    void deliverOrder_WithCompletedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setCourier(testCourier);
        order.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(order));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> courierOrderService.deliverOrder(orderId, testCourier));

        Assertions.assertEquals("Заказ с id '1' уже доставлен", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового курьера
     */
    private User createTestCourier() {
        User user = new User(
                "courier@example.com",
                "Test Courier",
                "+79991234579",
                Role.COURIER,
                "Склад №1"
        );
        user.setId(5L);
        return user;
    }

    /**
     * Создание другого тестового курьера
     */
    private User createDifferentCourier() {
        User user = new User(
                "other-courier@example.com",
                "Other Courier",
                "+79991234580",
                Role.COURIER,
                "Склад №2"
        );
        user.setId(6L);
        return user;
    }

    /**
     * Создает тестовый заказ
     */
    private Order createTestOrder() {
        Order order = new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                List.of(),
                new BigDecimal("500.00"),
                null,
                null
        );
        order.setId(1L);
        return order;
    }
}
