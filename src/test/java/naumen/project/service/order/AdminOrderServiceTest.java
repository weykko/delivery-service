package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
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
 * Модульные тесты для {@link AdminOrderService}
 */
@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminOrderService adminOrderService;

    private final User testClient = createTestUser(1L, Role.CLIENT);
    private final Order testOrder = createTestOrder(testClient);

    /**
     * Тестирование успешного удаления заказа с валидным статусом
     */
    @Test
    void deleteOrderWithValidStatusShouldDeleteOrder() {
        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        adminOrderService.deleteOrder(testOrder.getId());

        Assertions.assertEquals(OrderStatus.DELETED, testOrder.getStatus());
        Mockito.verify(orderService).getById(testOrder.getId());
        Mockito.verify(orderService).save(testOrder);
    }

    /**
     * Тестирование удаления завершенного заказа
     */
    @Test
    void deleteCompletedOrderShouldThrowException() {
        testOrder.setStatus(OrderStatus.COMPLETED);

        Mockito.when(orderService.getById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminOrderService.deleteOrder(testOrder.getId()));

        Assertions.assertEquals("Невозможно удалить завершенный заказ c id '1'", exception.getMessage());
        Mockito.verify(orderService).getById(testOrder.getId());
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование удаления несуществующего заказа
     */
    @Test
    void deleteOrderWithInvalidIdShouldThrowException() {
        Long invalidOrderId = 999L;

        Mockito.when(orderService.getById(invalidOrderId)).thenReturn(Optional.empty());

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminOrderService.deleteOrder(invalidOrderId));

        Assertions.assertEquals("Не удалось удалить заказ. Причина: Заказ с id '999' не найден",
                exception.getMessage());
        Mockito.verify(orderService).getById(invalidOrderId);
        Mockito.verify(orderService, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование успешного назначения курьера на заказ
     */
    @Test
    void setCourierByIdShouldSetCourier() {
        Long courierId = 2L;
        User testCourier = createTestUser(courierId, Role.COURIER);

        Mockito.when(userService.getUserById(courierId)).thenReturn(Optional.of(testCourier));

        adminOrderService.setCourierById(testOrder, courierId);

        Assertions.assertEquals(testCourier, testOrder.getCourier());
        Mockito.verify(userService).getUserById(testCourier.getId());
    }

    /**
     * Тестирование назначения несуществующего курьера
     */
    @Test
    void setCourierByIdWithInvalidIdShouldThrowException() {
        Long courierId = 999L;

        Mockito.when(userService.getUserById(courierId)).thenReturn(Optional.empty());

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminOrderService.setCourierById(testOrder, courierId));

        Assertions.assertEquals("Не удалось назначить курьера, причина: Пользователь с id '999' не найден",
                exception.getMessage());
        Mockito.verify(userService).getUserById(courierId);
    }

    /**
     * Тестирование назначения пользователя не с ролью курьера
     */
    @Test
    void setCourierByIdWithNonCourierUserShouldThrowException() {
        Long notCourierId = 3L;
        User testNotCourier = createTestUser(notCourierId, Role.CLIENT);

        Mockito.when(userService.getUserById(notCourierId)).thenReturn(Optional.of(testNotCourier));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminOrderService.setCourierById(testOrder, notCourierId));

        Assertions.assertEquals(
                "Не удалось назначить курьера, причина: Пользователь с id '3' не является курьером",
                exception.getMessage());
        Mockito.verify(userService).getUserById(testNotCourier.getId());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя
     */
    private User createTestUser(Long id, Role role) {
        User user = new User(
                "test@example.com",
                "Test User",
                "+79991234567",
                role,
                "Пушкина 17"
        );
        user.setId(id);

        return user;
    }

    /**
     * Создание тестового заказа
     */
    private Order createTestOrder(User client) {
        Order order = new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                List.of(),
                new BigDecimal("500.00"),
                null,
                client
        );
        order.setId(1L);

        return order;
    }
}

