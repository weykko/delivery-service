package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.ForbiddenException;
import naumen.project.exception.IllegalDataException;
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

import java.util.List;

/**
 * Модульные тесты для {@link OrderRestaurantService}
 */
@ExtendWith(MockitoExtension.class)
class OrderRestaurantServiceTest extends OrderTestBase {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderRestaurantService orderRestaurantService;

    private final User testRestaurant = createTestRestaurant();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование получения активных заказов ресторана
     */
    @Test
    void getActiveOrders_WithValidRestaurant_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));

        Mockito.when(orderService.getActiveOrdersByRestaurant(testRestaurant, pageable))
                .thenReturn(ordersPage);

        Page<Order> result = orderRestaurantService.getActiveOrders(testRestaurant, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(testOrder, result.getContent().getFirst());
        Mockito.verify(orderService).getActiveOrdersByRestaurant(testRestaurant, pageable);
    }

    /**
     * Тестирование успешного получения заказа ресторана
     */
    @Test
    void getOrder_WithValidRestaurantAndOrder_ShouldReturnOrder() {
        Long orderId = 1L;

        Mockito.when(orderService.getById(orderId)).thenReturn(testOrder);

        Order result = orderRestaurantService.getOrder(orderId, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование получения заказа чужим рестораном
     */
    @Test
    void getOrder_WithDifferentRestaurant_ShouldThrowException() {
        Long orderId = 1L;
        User differentRestaurant = createDifferentRestaurant();

        Mockito.when(orderService.getById(orderId)).thenReturn(testOrder);

        ForbiddenException exception = Assertions.assertThrows(ForbiddenException.class,
                () -> orderRestaurantService.getOrder(orderId, differentRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом CREATED
     */
    @Test
    void prepareOrder_WithCreatedStatus_ShouldChangeToAccepted() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);
        Mockito.when(orderService.save(order)).thenReturn(order);

        orderRestaurantService.prepareOrder(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(order);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом ACCEPTED
     */
    @Test
    void prepareOrder_WithAcceptedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.prepareOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("уже готовиться"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом PREPARED
     */
    @Test
    void prepareOrder_WithPreparedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.prepareOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("уже приготовлен"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом DELIVERING
     */
    @Test
    void prepareOrder_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.prepareOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("уже был отдан курьеру"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении чужим рестораном
     */
    @Test
    void prepareOrder_WithDifferentRestaurant_ShouldThrowException() {
        Long orderId = 1L;
        User differentRestaurant = createDifferentRestaurant();
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        ForbiddenException exception = Assertions.assertThrows(ForbiddenException.class,
                () -> orderRestaurantService.prepareOrder(orderId, differentRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом ACCEPTED
     */
    @Test
    void readyOrder_WithAcceptedStatus_ShouldChangeToPrepared() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);
        Mockito.when(orderService.save(order)).thenReturn(order);

        orderRestaurantService.readyOrder(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.PREPARED, order.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(order);
    }

    /**
     * Тестирование пометки заказа как готового со статусом CREATED
     */
    @Test
    void readyOrder_WithCreatedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.readyOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("еще не начал готовиться"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом PREPARED
     */
    @Test
    void readyOrder_WithPreparedStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.readyOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("уже приготовлен"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом DELIVERING
     */
    @Test
    void readyOrder_WithDeliveringStatus_ShouldThrowException() {
        Long orderId = 1L;
        Order order = createTestOrder();
        order.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> orderRestaurantService.readyOrder(orderId, testRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("уже был отдан курьеру"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового чужим рестораном
     */
    @Test
    void readyOrder_WithDifferentRestaurant_ShouldThrowException() {
        Long orderId = 1L;
        User differentRestaurant = createDifferentRestaurant();
        Order order = createTestOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(order);

        ForbiddenException exception = Assertions.assertThrows(ForbiddenException.class,
                () -> orderRestaurantService.readyOrder(orderId, differentRestaurant));

        Assertions.assertTrue(exception.getMessage().contains("не принадлежит вашему ресторану"));
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }
}
