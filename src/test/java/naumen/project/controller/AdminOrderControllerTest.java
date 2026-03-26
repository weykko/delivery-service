package naumen.project.controller;

import naumen.project.dto.order.admin.OrderAdminResponseDto;
import naumen.project.dto.order.admin.OrderAdminShortResponseDto;
import naumen.project.dto.order.admin.OrderAdminUpdateRequestDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.InvalidInputException;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.order.AdminOrderService;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Модульные тесты для {@link AdminOrderController}
 */
@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @Mock
    private AdminOrderService adminOrderService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private AdminOrderController adminOrderController;

    private final User testClient = createTestUser();
    private final Order testOrder = createTestOrder(testClient);

    /**
     * Тестирование успешного получения списка всех заказов с пагинацией
     */
    @Test
    void getOrdersShouldReturnPagedOrders() {
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        OrderAdminShortResponseDto shortDto = new OrderAdminShortResponseDto(
                1L, OrderStatus.CREATED, 1L, 2L, null, new BigDecimal("100.00"), Instant.now()
        );
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        PagedResponseDto<OrderAdminShortResponseDto> expectedResponse = new PagedResponseDto<>(
                List.of(shortDto),
                page,
                size,
                1,
                1
        );

        Mockito.when(adminOrderService.getOrders(null, pageRequest)).thenReturn(orderPage);
        Mockito.when(orderMapper.toAdminShortResponse(testOrder)).thenReturn(shortDto);
        Mockito.when(pageMapper.toOrderAdminResponse(Mockito.any())).thenReturn(expectedResponse);

        PagedResponseDto<OrderAdminShortResponseDto> result = adminOrderController.getOrders(null, page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Mockito.verify(adminOrderService).getOrders(null, pageRequest);
        Mockito.verify(orderMapper).toAdminShortResponse(testOrder);
        Mockito.verify(pageMapper).toOrderAdminResponse(Mockito.any());
    }

    /**
     * Тестирование успешного получения заказа по идентификатору
     */
    @Test
    void getOrderShouldReturnOrderDetails() {
        Long orderId = 1L;
        OrderAdminResponseDto expectedResponse = new OrderAdminResponseDto(
                1L, OrderStatus.CREATED, 1L, 2L, null, new BigDecimal("100.00"), "Address", Instant.now(), List.of()
        );

        Mockito.when(adminOrderService.getOrderById(orderId)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderMapper.toAdminResponse(testOrder)).thenReturn(expectedResponse);

        OrderAdminResponseDto result = adminOrderController.getOrder(orderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Mockito.verify(adminOrderService).getOrderById(orderId);
        Mockito.verify(orderMapper).toAdminResponse(testOrder);
    }

    /**
     * Тестирование получения несуществующего заказа
     */
    @Test
    void getOrderNotFoundShouldThrowException() {
        Long orderId = 999L;
        Mockito.when(adminOrderService.getOrderById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> adminOrderController.getOrder(orderId));
        Mockito.verify(adminOrderService).getOrderById(orderId);
    }

    /**
     * Тестирование успешного обновления заказа
     */
    @Test
    void updateOrderShouldUpdateAndReturnOrder() {
        Long orderId = 1L;
        OrderAdminUpdateRequestDto updateRequest = new OrderAdminUpdateRequestDto(
                OrderStatus.ACCEPTED, 3L, new BigDecimal("150.00")
        );
        OrderAdminResponseDto expectedResponse = new OrderAdminResponseDto(
                testOrder.getId(),
                updateRequest.status(),
                1L,
                2L,
                updateRequest.courierId(),
                updateRequest.totalPrice(),
                testOrder.getDeliveryAddress(),
                testOrder.getCreatedAt(),
                List.of()
        );

        Mockito.when(adminOrderService.getOrderById(orderId)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderMapper.toAdminResponse(testOrder)).thenReturn(expectedResponse);

        OrderAdminResponseDto result = adminOrderController.updateOrder(orderId, updateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Mockito.verify(adminOrderService).getOrderById(orderId);
        Mockito.verify(adminOrderService).setCourierById(testOrder, 3L);
        Mockito.verify(adminOrderService).saveOrder(testOrder);
        Mockito.verify(orderMapper).toAdminResponse(testOrder);
    }

    /**
     * Тестирование обновления несуществующего заказа
     */
    @Test
    void updateOrderWithInvalidIdShouldThrowException() {
        Long orderId = 999L;
        OrderAdminUpdateRequestDto updateRequest = new OrderAdminUpdateRequestDto(
                OrderStatus.ACCEPTED, null, null
        );

        Mockito.when(adminOrderService.getOrderById(orderId)).thenReturn(Optional.empty());

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminOrderController.updateOrder(orderId, updateRequest));

        Assertions.assertEquals("Не удалось обновить заказ, причина: Заказ с id '999' не найден",
                exception.getMessage());
        Mockito.verify(adminOrderService).getOrderById(orderId);
        Mockito.verify(adminOrderService, Mockito.never()).saveOrder(Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Метод для создания тестового пользователя
     */
    private User createTestUser() {
        User user = new User(
                "test@example.com",
                "Test User",
                "+79991234567",
                Role.CLIENT,
                "Пушкина 17"
        );
        user.setId(1L);

        return user;
    }

    /**
     * Метод для создания тестового заказа
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

