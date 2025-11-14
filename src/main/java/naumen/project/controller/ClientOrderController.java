package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.order.client.OrderClientCreateRequestDto;
import naumen.project.dto.order.client.OrderClientCreateResponseDto;
import naumen.project.dto.order.client.OrderClientInfoResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.mapper.OrderMapper;
import naumen.project.service.OrderItemService;
import naumen.project.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Работа с заказами для клиентов
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/client/orders")
public class ClientOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;

    public ClientOrderController(
            OrderService orderService,
            OrderItemService orderItemService,
            OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.orderMapper = orderMapper;
    }

    /**
     * Создание заказа. Заказ получает статус CREATED
     *
     * @param request запрос на создание
     * @param user    клиент, который оформляет заказ
     * @return информация о заказе
     */
    @PostMapping
    @Transactional
    public OrderClientCreateResponseDto createOrder(
            @RequestBody @Valid OrderClientCreateRequestDto request,
            @AuthenticationPrincipal User user
    ) {
        List<OrderItem> orderItemList = request.items().stream()
                .map(orderItem -> orderItemService.buildOrderItem(orderItem.menuItemId(), orderItem.quantity()))
                .toList();
        Order order = orderService.createOrderByClient(
                request.restaurantId(),
                orderItemList,
                request.deliveryAddress(),
                user
        );
        return orderMapper.toClientCreateResponse(order);
    }

    /**
     * Получение информации о заказе по его id.
     * Проверяется доступ, что заказ принадлежит текущему клиенту
     *
     * @param orderId id заказа
     * @param user    текущий клиент
     * @return информация о заказе или 403 с ошибкой, если заказ не принадлежит текущему клиенту
     */
    @GetMapping("/{orderId}")
    @Transactional(readOnly = true)
    public OrderClientInfoResponseDto getOrderInfoById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) {
        Order order = orderService.getOrderByClient(orderId, user);
        return orderMapper.toClientInfoResponse(order);
    }

    /**
     * Удаление заказа. Статус заказа переводится в DELETED
     *
     * @param orderId id заказа
     * @param user    текущий клиент
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    @Transactional
    public void deleteOrderByClient(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
        orderService.deleteOrderByClient(orderId, user);
    }

}
