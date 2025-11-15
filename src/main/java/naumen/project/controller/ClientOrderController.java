package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.order.client.OrderClientCreateRequestDto;
import naumen.project.dto.order.client.OrderClientInfoResponseDto;
import naumen.project.dto.order.client.OrderClientShortResponseDto;
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
 * Контроллер для работы с заказами со стороны клиента
 * Предоставляет endpoints для создания, получения информации и удаления заказов
 * Требует аутентификации с JWT токеном и права доступа CLIENT
 *
 * @see OrderService
 * @see OrderItemService
 * @see OrderMapper
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
     * Создание заказа. Заказ получает статус CREATED.
     * Все позиции заказа должны принадлежать указанному ресторану
     *
     * @param request запрос на создание
     * @param client  клиент, который оформляет заказ
     * @return информация о заказе
     */
    @PostMapping
    @Transactional
    public OrderClientInfoResponseDto createOrder(
            @RequestBody @Valid OrderClientCreateRequestDto request,
            @AuthenticationPrincipal User client
    ) {
        List<OrderItem> orderItemList = request.items().stream()
                .map(orderItem -> orderItemService.buildOrderItem(orderItem.menuItemId(), orderItem.quantity()))
                .toList();
        Order order = orderService.createOrderByClient(
                request.restaurantId(),
                orderItemList,
                request.deliveryAddress(),
                client
        );
        return orderMapper.toClientInfoResponse(order);
    }

    /**
     * Получение списка заказов текущего клиента
     *
     * @param client текущий клиент
     * @return страница с заказами клиента
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public List<OrderClientShortResponseDto> getOrders(
            @AuthenticationPrincipal User client
    ) {
        return orderService.getOrdersByClient(client).stream()
                .map(orderMapper::toClientShortResponse)
                .toList();
    }

    /**
     * Получение информации о заказе по его id.
     * Проверяется доступ, что заказ принадлежит текущему клиенту
     *
     * @param orderId id заказа
     * @param client  текущий клиент
     * @return информация о заказе или 403 с ошибкой, если заказ не принадлежит текущему клиенту
     */
    @GetMapping("/{orderId}")
    @Transactional(readOnly = true)
    public OrderClientInfoResponseDto getOrderInfoById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User client
    ) {
        Order order = orderService.getOrderByClient(orderId, client);
        return orderMapper.toClientInfoResponse(order);
    }

    /**
     * Удаление заказа. Статус заказа переводится в DELETED
     *
     * @param orderId id заказа
     * @param client  текущий клиент
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    @Transactional
    public void deleteOrderByClient(@PathVariable Long orderId,
                                    @AuthenticationPrincipal User client) {
        orderService.deleteOrderByClient(orderId, client);
    }
}
