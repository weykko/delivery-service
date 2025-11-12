package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import naumen.project.dto.order.OrderCourierResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.User;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления курьерами ресторана.
 * Предоставляет endpoints для операций с курьерами.
 * Требует аутентификации с JWT токеном и права доступа DELIVERY.
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/courier")
public class CourierController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final PageMapper pageMapper;

    public  CourierController(OrderService orderService, OrderMapper orderMapper, PageMapper pageMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.pageMapper = pageMapper;
    }

    /**
     * Получает заказы, доступные курьеру.
     *
     * @return страница с доступными заказами
     */
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public PagedResponseDto<OrderCourierResponseDto> getAvailableOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderCourierResponseDto> orderPages = orderService
                .getAvailableOrders(PageRequest.of(page, size))
                .map(orderMapper::toResponse);

        return pageMapper.toOrderResponse(orderPages);
    }

    /**
     * Получает активные заказы курьера.
     *
     * @return страница с активными заказами курьера
     */
    @GetMapping("/orders/active")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public List<OrderCourierResponseDto> getActiveOrders(@AuthenticationPrincipal User courier) {
        return orderService.getActiveOrdersByCourier(courier).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Принимает заказ курьером.
     *
     * @param orderId идентификатор заказа
     */
    @PatchMapping("/orders/{orderId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void acceptOrder(@PathVariable Long orderId,
                            @AuthenticationPrincipal User courier) {
        orderService.acceptOrderByCourier(orderId, courier);
    }

    /**
     * Помечает заказ как забранный курьером.
     *
     * @param orderId идентификатор заказа
     */
    @PatchMapping("/orders/{orderId}/pick-up")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void pickUpOrder(@PathVariable Long orderId,
                            @AuthenticationPrincipal User courier) {
        orderService.pickUpOrderByCourier(orderId, courier);
    }

    /**
     * Помечает заказ как доставленный курьером.
     *
     * @param orderId идентификатор заказа
     */
    @PatchMapping("/orders/{orderId}/deliver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deliverOrder(@PathVariable Long orderId,
                             @AuthenticationPrincipal User courier) {
        orderService.deliverOrderByCourier(orderId, courier);
    }
}
