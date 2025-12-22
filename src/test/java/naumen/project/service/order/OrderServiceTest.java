package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
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

    @InjectMocks
    private OrderService orderService;

    private final User testClient = createTestClient();
    private final User testRestaurant = createTestRestaurant();
    private final User testCourier = createTestCourier();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование успешного получения заказа по идентификатору
     */
    @Test
    void getById_WithExistingOrder_ShouldReturnOrder() {
        Long orderId = 1L;

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Ожидали получить заказ, но он не был найден"));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderRepository).findById(orderId);
    }

    /**
     * Тестирование успешного сохранения заказа
     */
    @Test
    void save_WithValidOrder_ShouldReturnSavedOrder() {
        Mockito.when(orderRepository.save(testOrder)).thenReturn(testOrder);

        Order result = orderService.save(testOrder);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testOrder, result);
        Mockito.verify(orderRepository).save(testOrder);
    }

    /**
     * Тестирование получения заказов по клиенту
     */
    @Test
    void getByClient_WithValidClient_ShouldReturnOrdersList() {
        List<Order> orders = List.of(testOrder);

        Mockito.when(orderRepository.findOrdersByClient(testClient)).thenReturn(orders);

        List<Order> result = orderService.getByClient(testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testOrder, result.getFirst());
        Mockito.verify(orderRepository).findOrdersByClient(testClient);
    }

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
     * Создает тестовый заказ
     */
    private Order createTestOrder() {
        Order order = new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                List.of(),
                new BigDecimal("500.00"),
                null,
                createTestClient()
        );
        order.setId(1L);
        return order;
    }
}
