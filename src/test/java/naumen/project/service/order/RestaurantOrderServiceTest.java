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
 * Модульные тесты для {@link RestaurantOrderService}
 */
@ExtendWith(MockitoExtension.class)
class RestaurantOrderServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private RestaurantOrderService restaurantOrderService;

    private final User testRestaurant = createTestRestaurant();
    private final Order testOrder = createTestOrder();
    private final Long orderId = 1L;

    /**
     * Тестирование получения активных заказов ресторана
     */
    @Test
    void getActiveOrders_WithValidRestaurant_ShouldReturnPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));

        Mockito.when(orderService.getActiveOrdersByRestaurant(testRestaurant, pageable))
                .thenReturn(ordersPage);

        Page<Order> result = restaurantOrderService.getActiveOrders(testRestaurant, pageable);

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
        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        Order result = restaurantOrderService.getOrder(orderId, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование получения заказа чужим рестораном
     */
    @Test
    void getOrder_WithDifferentRestaurant_ShouldThrowException() {
        User differentRestaurant = createDifferentRestaurant();

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> restaurantOrderService.getOrder(orderId, differentRestaurant));

        Assertions.assertEquals("Заказ с id '1' не принадлежит вашему ресторану", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом CREATED
     */
    @Test
    void prepareOrder_WithCreatedStatus_ShouldChangeToAccepted() {
        testOrder.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderService.save(testOrder)).thenReturn(testOrder);

        restaurantOrderService.prepareOrder(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.ACCEPTED, testOrder.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(testOrder);
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом ACCEPTED
     */
    @Test
    void prepareOrder_WithAcceptedStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.prepareOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' уже готовится", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом PREPARED
     */
    @Test
    void prepareOrder_WithPreparedStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.prepareOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' уже приготовлен", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении со статусом DELIVERING
     */
    @Test
    void prepareOrder_WithDeliveringStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.prepareOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' уже был отдан курьеру", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как в приготовлении чужим рестораном
     */
    @Test
    void prepareOrder_WithDifferentRestaurant_ShouldThrowException() {
        User differentRestaurant = createDifferentRestaurant();
        testOrder.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> restaurantOrderService.prepareOrder(orderId, differentRestaurant));

        Assertions.assertEquals("Заказ с id '1' не принадлежит вашему ресторану", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом ACCEPTED
     */
    @Test
    void readyOrder_WithAcceptedStatus_ShouldChangeToPrepared() {
        testOrder.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderService.save(testOrder)).thenReturn(testOrder);

        restaurantOrderService.readyOrder(orderId, testRestaurant);

        Assertions.assertEquals(OrderStatus.PREPARED, testOrder.getStatus());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService).save(testOrder);
    }

    /**
     * Тестирование пометки заказа как готового со статусом CREATED
     */
    @Test
    void readyOrder_WithCreatedStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.CREATED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.readyOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' еще не начал готовиться", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом PREPARED
     */
    @Test
    void readyOrder_WithPreparedStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.PREPARED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.readyOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' уже приготовлен", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового со статусом DELIVERING
     */
    @Test
    void readyOrder_WithDeliveringStatus_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.DELIVERING);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> restaurantOrderService.readyOrder(orderId, testRestaurant));

        Assertions.assertEquals("Заказ с id '1' уже был отдан курьеру", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование пометки заказа как готового чужим рестораном
     */
    @Test
    void readyOrder_WithDifferentRestaurant_ShouldThrowException() {
        User differentRestaurant = createDifferentRestaurant();
        testOrder.setStatus(OrderStatus.ACCEPTED);

        Mockito.when(orderService.getById(orderId)).thenReturn(Optional.of(testOrder));

        PermissionCheckFailedException exception = Assertions.assertThrows(PermissionCheckFailedException.class,
                () -> restaurantOrderService.readyOrder(orderId, differentRestaurant));

        Assertions.assertEquals("Заказ с id '1' не принадлежит вашему ресторану", exception.getMessage());
        Mockito.verify(orderService).getById(orderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

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
     * Создание другого тестового ресторана
     */
    private User createDifferentRestaurant() {
        User user = new User(
                "other-restaurant@example.com",
                "Other Restaurant",
                "+79991234578",
                Role.RESTAURANT,
                "Ленина 5"
        );
        user.setId(4L);
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
                createTestRestaurant(),
                null
        );
        order.setId(1L);
        return order;
    }
}
