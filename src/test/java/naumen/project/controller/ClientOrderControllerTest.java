package naumen.project.controller;

import naumen.project.dto.order.client.OrderClientCreateRequestDto;
import naumen.project.dto.order.client.OrderClientResponseDto;
import naumen.project.dto.order.client.OrderClientShortResponseDto;
import naumen.project.dto.order.item.OrderItemCreateRequestDto;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.OrderMapper;
import naumen.project.service.OrderItemService;
import naumen.project.service.order.ClientOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Модульные тесты для {@link ClientOrderController}
 */
@ExtendWith(MockitoExtension.class)
class ClientOrderControllerTest {

    @Mock
    private ClientOrderService clientOrderService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private ClientOrderController clientOrderController;

    private final User testClient = createTestClient();
    private final Order testOrder = createTestOrder();

    /**
     * Тестирование успешного создания заказа с валидными данными
     */
    @Test
    void createOrderWithValidRequestShouldReturnOrderInfo() {
        OrderClientCreateRequestDto createRequest = createOrderCreateRequest();
        OrderItem orderItem = createOrderItem();
        List<OrderItem> orderItemList = List.of(orderItem);

        OrderClientResponseDto expectedResponse = new OrderClientResponseDto(
                1L,
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "Ул Пушкина",
                1L,
                1L,
                Instant.now(),
                List.of()
        );

        Mockito.when(orderItemService.buildOrderItem(1L, 2)).thenReturn(orderItem);
        Mockito.when(clientOrderService.createOrder(
                createRequest.restaurantId(),
                orderItemList,
                createRequest.deliveryAddress(),
                testClient
        )).thenReturn(testOrder);
        Mockito.when(orderMapper.toClientResponse(testOrder)).thenReturn(expectedResponse);

        OrderClientResponseDto result = clientOrderController.createOrder(createRequest, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.status(), result.status());
        Assertions.assertEquals(expectedResponse.totalPrice(), result.totalPrice());
        Assertions.assertEquals(expectedResponse.deliveryAddress(), result.deliveryAddress());
        Mockito.verify(orderItemService).buildOrderItem(1L, 2);
        Mockito.verify(clientOrderService).createOrder(
                createRequest.restaurantId(),
                orderItemList,
                createRequest.deliveryAddress(),
                testClient
        );
        Mockito.verify(orderMapper).toClientResponse(testOrder);
    }

    /**
     * Тестирование получения списка заказов текущего клиента
     */
    @Test
    void getOrdersWithAuthenticatedClientShouldReturnOrdersList() {
        OrderClientShortResponseDto orderResponse = new OrderClientShortResponseDto(
                1L,
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "Test Restaurant"
        );

        Mockito.when(clientOrderService.getOrders(testClient)).thenReturn(List.of(testOrder));
        Mockito.when(orderMapper.toClientShortResponse(testOrder)).thenReturn(orderResponse);

        List<OrderClientShortResponseDto> result = clientOrderController.getOrders(testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderResponse.id(), result.getFirst().id());
        Assertions.assertEquals(orderResponse.status(), result.getFirst().status());
        Assertions.assertEquals(orderResponse.totalPrice(), result.getFirst().totalPrice());
        Mockito.verify(clientOrderService).getOrders(testClient);
        Mockito.verify(orderMapper).toClientShortResponse(testOrder);
    }

    /**
     * Тестирование получения информации о заказе по идентификатору
     */
    @Test
    void getOrderByIdWithValidIdShouldReturnOrder() {
        Long orderId = 1L;
        OrderClientResponseDto expectedResponse = new OrderClientResponseDto(
                orderId,
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "Ул Пушкина",
                1L,
                1L,
                Instant.now(),
                List.of()
        );

        Mockito.when(clientOrderService.getOrder(orderId, testClient)).thenReturn(testOrder);
        Mockito.when(orderMapper.toClientResponse(testOrder)).thenReturn(expectedResponse);

        OrderClientResponseDto result = clientOrderController.getOrder(orderId, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.status(), result.status());
        Assertions.assertEquals(expectedResponse.deliveryAddress(), result.deliveryAddress());
        Mockito.verify(clientOrderService).getOrder(orderId, testClient);
        Mockito.verify(orderMapper).toClientResponse(testOrder);
    }

    /**
     * Тестирование успешного удаления заказа клиентом
     */
    @Test
    void deleteOrderByClient_WithValidOrder_ShouldCallDeleteService() {
        Long orderId = 1L;

        clientOrderController.deleteOrderByClient(orderId, testClient);

        Mockito.verify(clientOrderService).deleteOrder(orderId, testClient);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового клиента
     */
    private User createTestClient() {
        return new User(
                "client@example.com",
                "Test Client",
                "+79991234567",
                Role.CLIENT,
                "Пушкина 17"
        );
    }

    /**
     * Создает тестовый заказ
     */
    private Order createTestOrder() {
        return new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                List.of(),
                new BigDecimal("500.00"),
                null,
                testClient
        );
    }

    /**
     * Создает тестовый запрос на создание заказа
     */
    private OrderClientCreateRequestDto createOrderCreateRequest() {
        List<OrderItemCreateRequestDto> items = List.of(
                new OrderItemCreateRequestDto(1L, 2)
        );
        return new OrderClientCreateRequestDto(
                1L,
                "Ул Пушкина",
                items
        );
    }

    /**
     * Создает тестовый элемент заказа
     */
    private OrderItem createOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(2);
        orderItem.setItemPrice(new BigDecimal("250.00"));
        return orderItem;
    }
}
