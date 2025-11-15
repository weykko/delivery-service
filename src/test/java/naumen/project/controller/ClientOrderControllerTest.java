package naumen.project.controller;

import naumen.project.dto.order.client.OrderClientCreateRequestDto;
import naumen.project.dto.order.client.OrderClientInfoResponseDto;
import naumen.project.dto.order.client.OrderClientShortResponseDto;
import naumen.project.dto.order.item.OrderItemCreateRequestDto;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.OrderMapper;
import naumen.project.service.OrderItemService;
import naumen.project.service.OrderService;
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
    private OrderService orderService;

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
    void createOrder_WithValidRequest_ShouldReturnOrderInfo() {
        OrderClientCreateRequestDto createRequest = createOrderCreateRequest();
        OrderItem orderItem = createOrderItem();
        List<OrderItem> orderItemList = List.of(orderItem);

        OrderClientInfoResponseDto expectedResponse = new OrderClientInfoResponseDto(
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
        Mockito.when(orderService.createOrderByClient(
                createRequest.restaurantId(),
                orderItemList,
                createRequest.deliveryAddress(),
                testClient
        )).thenReturn(testOrder);
        Mockito.when(orderMapper.toClientInfoResponse(testOrder)).thenReturn(expectedResponse);

        OrderClientInfoResponseDto result = clientOrderController.createOrder(createRequest, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.status(), result.status());
        Assertions.assertEquals(expectedResponse.totalPrice(), result.totalPrice());
        Assertions.assertEquals(expectedResponse.deliveryAddress(), result.deliveryAddress());
        Mockito.verify(orderItemService).buildOrderItem(1L, 2);
        Mockito.verify(orderService).createOrderByClient(
                createRequest.restaurantId(),
                orderItemList,
                createRequest.deliveryAddress(),
                testClient
        );
        Mockito.verify(orderMapper).toClientInfoResponse(testOrder);
    }

    /**
     * Тестирование получения списка заказов текущего клиента
     */
    @Test
    void getOrders_WithAuthenticatedClient_ShouldReturnOrdersList() {
        OrderClientShortResponseDto orderResponse = new OrderClientShortResponseDto(
                1L,
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "Test Restaurant"
        );

        Mockito.when(orderService.getOrdersByClient(testClient)).thenReturn(List.of(testOrder));
        Mockito.when(orderMapper.toClientShortResponse(testOrder)).thenReturn(orderResponse);

        List<OrderClientShortResponseDto> result = clientOrderController.getOrders(testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderResponse.id(), result.get(0).id());
        Assertions.assertEquals(orderResponse.status(), result.get(0).status());
        Assertions.assertEquals(orderResponse.totalPrice(), result.get(0).totalPrice());
        Mockito.verify(orderService).getOrdersByClient(testClient);
        Mockito.verify(orderMapper).toClientShortResponse(testOrder);
    }

    /**
     * Тестирование получения информации о заказе по идентификатору
     */
    @Test
    void getOrderInfoById_WithValidId_ShouldReturnOrderInfo() {
        Long orderId = 1L;
        OrderClientInfoResponseDto expectedResponse = new OrderClientInfoResponseDto(
                orderId,
                OrderStatus.CREATED,
                new BigDecimal("500.00"),
                "Ул Пушкина",
                1L,
                1L,
                Instant.now(),
                List.of()
        );

        Mockito.when(orderService.getOrderByClient(orderId, testClient)).thenReturn(testOrder);
        Mockito.when(orderMapper.toClientInfoResponse(testOrder)).thenReturn(expectedResponse);

        OrderClientInfoResponseDto result = clientOrderController.getOrderInfoById(orderId, testClient);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.status(), result.status());
        Assertions.assertEquals(expectedResponse.deliveryAddress(), result.deliveryAddress());
        Mockito.verify(orderService).getOrderByClient(orderId, testClient);
        Mockito.verify(orderMapper).toClientInfoResponse(testOrder);
    }

    /**
     * Тестирование успешного удаления заказа клиентом
     */
    @Test
    void deleteOrderByClient_WithValidOrder_ShouldCallDeleteService() {
        Long orderId = 1L;

        clientOrderController.deleteOrderByClient(orderId, testClient);

        Mockito.verify(orderService).deleteOrderByClient(orderId, testClient);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового клиента
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
     * Создает тестовый заказ
     */
    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(new BigDecimal("500.00"));
        order.setDeliveryAddress("Ул Пушкина");
        order.setClient(testClient);
        return order;
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
