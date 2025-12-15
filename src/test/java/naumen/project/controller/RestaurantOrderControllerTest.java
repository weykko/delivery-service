package naumen.project.controller;

import naumen.project.dto.order.restaurant.OrderRestaurantResponseDto;
import naumen.project.dto.order.restaurant.OrderRestaurantShortResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.order.OrderRestaurantService;
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

/**
 * Модульные тесты для {@link RestaurantOrderController}
 */
@ExtendWith(MockitoExtension.class)
class RestaurantOrderControllerTest {

    @Mock
    private OrderRestaurantService orderRestaurantService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private RestaurantOrderController restaurantOrderController;

    /**
     * Тестирование получения активных заказов ресторана
     */
    @Test
    void getActiveOrders_ShouldReturnPagedOrders() {
        Order testOrder = createTestOrder();
        User testRestaurant = createTestRestaurant();
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder));
        OrderRestaurantShortResponseDto responseDto = createOrderRestaurantShortResponseDto(testOrder);
        Page<OrderRestaurantShortResponseDto> responsePage = new PageImpl<>(List.of(responseDto));
        PagedResponseDto<OrderRestaurantShortResponseDto> pagedResponse = new PagedResponseDto<>(
                List.of(responseDto), page, size, 1, 1
        );

        Mockito.when(orderRestaurantService.getActiveOrders(testRestaurant, pageRequest)).thenReturn(orderPage);
        Mockito.when(orderMapper.toRestaurantShortResponse(testOrder)).thenReturn(responseDto);
        Mockito.when(pageMapper.toOrderRestaurantResponse(responsePage)).thenReturn(pagedResponse);

        PagedResponseDto<OrderRestaurantShortResponseDto> result = restaurantOrderController.getActiveOrders(page, size, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.content().size());
        Assertions.assertEquals(responseDto, result.content().getFirst());
        Mockito.verify(orderRestaurantService).getActiveOrders(testRestaurant, pageRequest);
        Mockito.verify(orderMapper).toRestaurantShortResponse(testOrder);
        Mockito.verify(pageMapper).toOrderRestaurantResponse(responsePage);
    }

    /**
     * Тестирование получения информации о заказе
     */
    @Test
    void getOrder_ShouldReturnOrderInfo() {
        Order testOrder = createTestOrder();
        User testRestaurant = createTestRestaurant();
        Long orderId = 1L;
        OrderRestaurantResponseDto responseDto = createOrderRestaurantResponseDto(testOrder);

        Mockito.when(orderRestaurantService.getOrder(orderId, testRestaurant)).thenReturn(testOrder);
        Mockito.when(orderMapper.toRestaurantResponse(testOrder)).thenReturn(responseDto);

        OrderRestaurantResponseDto result = restaurantOrderController.getOrder(orderId, testRestaurant);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(responseDto, result);
        Mockito.verify(orderRestaurantService).getOrder(orderId, testRestaurant);
        Mockito.verify(orderMapper).toRestaurantResponse(testOrder);
    }

    // Вспомогательные методы

    private User createTestRestaurant() {
        User user = new User(
                "restaurant@example.com",
                "Test Restaurant",
                "+79991234567",
                Role.RESTAURANT
        );
        user.setId(3L);

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

    private OrderRestaurantShortResponseDto createOrderRestaurantShortResponseDto(Order order) {
        return new OrderRestaurantShortResponseDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice()
        );
    }

    private OrderRestaurantResponseDto createOrderRestaurantResponseDto(Order order) {
        return new OrderRestaurantResponseDto(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                null,
                Instant.now(),
                List.of()
        );
    }
}

