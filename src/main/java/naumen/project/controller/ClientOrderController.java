package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.order.client.OrderClientCreateRequestDto;
import naumen.project.dto.order.client.OrderClientResponseDto;
import naumen.project.dto.order.client.OrderClientShortResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.mapper.OrderMapper;
import naumen.project.service.OrderItemService;
import naumen.project.service.order.OrderClientService;
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
 * @see OrderClientService
 * @see OrderItemService
 * @see OrderMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/client/orders")
public class ClientOrderController {

    private final OrderClientService orderClientService;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;

    public ClientOrderController(
            OrderClientService orderClientService,
            OrderItemService orderItemService,
            OrderMapper orderMapper) {
        this.orderClientService = orderClientService;
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
    public OrderClientResponseDto createOrder(
            @RequestBody @Valid OrderClientCreateRequestDto request,
            @AuthenticationPrincipal User client
    ) {
        List<OrderItem> orderItemList = request.items().stream()
                .map(orderItem -> orderItemService.buildOrderItem(orderItem.menuItemId(), orderItem.quantity()))
                .toList();

        Order order = orderClientService.createOrder(
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
        return orderClientService.getOrders(client).stream()
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
    public OrderClientResponseDto getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User client
    ) {
        Order order = orderClientService.getOrder(orderId, client);
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
        orderClientService.deleteOrder(orderId, client);
    }
}
