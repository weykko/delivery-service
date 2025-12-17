package naumen.project.controller;

import naumen.project.dto.order.courier.OrderCourierResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.order.OrderCourierService;
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
import java.util.List;

/**
 * Модульные тесты для {@link CourierOrderController}
 */
@ExtendWith(MockitoExtension.class)
class CourierOrderControllerTest {

    @Mock
    private OrderCourierService orderCourierService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private CourierOrderController courierOrderController;

    /**
     * Тестирование получения доступных заказов для курьера
     */
    @Test
    void getAvailableOrdersShouldReturnPagedOrders() {
        Order testOrder = createTestOrder();
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        OrderCourierResponseDto responseDto = createOrderCourierResponseDto(testOrder);
        Page<OrderCourierResponseDto> responsePage = new PageImpl<>(List.of(responseDto));
        PagedResponseDto<OrderCourierResponseDto> pagedResponse = new PagedResponseDto<>(
                List.of(responseDto), page, size, 1, 1
        );

        Mockito.when(orderCourierService.getAvailableOrders(pageRequest)).thenReturn(orderPage);
        Mockito.when(orderMapper.toCourierResponse(testOrder)).thenReturn(responseDto);
        Mockito.when(pageMapper.toOrderCourierResponse(responsePage)).thenReturn(pagedResponse);

        PagedResponseDto<OrderCourierResponseDto> result = courierOrderController.getAvailableOrders(page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.content().size());
        Assertions.assertEquals(responseDto, result.content().getFirst());
        Mockito.verify(orderCourierService).getAvailableOrders(pageRequest);
        Mockito.verify(orderMapper).toCourierResponse(testOrder);
        Mockito.verify(pageMapper).toOrderCourierResponse(responsePage);
    }

    /**
     * Тестирование получения активных заказов курьера
     */
    @Test
    void getActiveOrdersShouldReturnListOfOrders() {
        User testCourier = createTestCourier();
        Order testOrder = createTestOrder();

        OrderCourierResponseDto responseDto = createOrderCourierResponseDto(testOrder);

        Mockito.when(orderCourierService.getActiveOrders(testCourier)).thenReturn(List.of(testOrder));
        Mockito.when(orderMapper.toCourierResponse(testOrder)).thenReturn(responseDto);

        List<OrderCourierResponseDto> result = courierOrderController.getActiveOrders(testCourier);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(responseDto, result.getFirst());
        Mockito.verify(orderCourierService).getActiveOrders(testCourier);
        Mockito.verify(orderMapper).toCourierResponse(testOrder);
    }

    // Вспомогательные методы

    private User createTestCourier() {
        User user = new User(
                "courier@example.com",
                "Test Courier",
                "+1234567890",
                Role.COURIER
        );
        user.setId(5L);

        return user;
    }

    private Order createTestOrder() {
        Order order = new Order(
                "Ул Пушкина",
                OrderStatus.CREATED,
                null,
                new BigDecimal("500.00"),
                null,
                null
        );
        order.setId(1L);

        return order;
    }

    private OrderCourierResponseDto createOrderCourierResponseDto(Order order) {
        return new OrderCourierResponseDto(
                order.getId(),
                order.getStatus(),
                "Restaurant Address",
                order.getDeliveryAddress(),
                order.getTotalPrice()
        );
    }
}

