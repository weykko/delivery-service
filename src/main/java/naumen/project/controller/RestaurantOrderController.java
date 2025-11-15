package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import naumen.project.dto.order.restaurant.OrderRestaurantResponseDto;
import naumen.project.dto.order.restaurant.OrderRestaurantShortResponseDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.order.OrderRestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления заказами ресторана.
 * Предоставляет endpoints для операций с заказами ресторана.
 * Требует аутентификации с JWT токеном и права доступа RESTAURANT.
 *
 * @see OrderRestaurantService
 * @see OrderMapper
 * @see PageMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/restaurant/orders")
public class RestaurantOrderController {

    private final OrderRestaurantService orderRestaurantService;
    private final OrderMapper orderMapper;
    private final PageMapper pageMapper;

    public RestaurantOrderController(
            OrderRestaurantService orderRestaurantService,
            OrderMapper orderMapper,
            PageMapper pageMapper
    ) {
        this.orderRestaurantService = orderRestaurantService;
        this.orderMapper = orderMapper;
        this.pageMapper = pageMapper;
    }

    /**
     * Получает активные заказы ресторана.
     *
     * @param page       номер страницы
     * @param size       количество элементов на странице
     * @param restaurant аутентифицированный ресторан
     * @return страница с активными заказами ресторана
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public PagedResponseDto<OrderRestaurantShortResponseDto> getActiveOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User restaurant) {
        Page<OrderRestaurantShortResponseDto> orderPages = orderRestaurantService
                .getActiveOrders(restaurant, PageRequest.of(page, size))
                .map(orderMapper::toRestaurantShortResponse);

        return pageMapper.toOrderRestaurantResponse(orderPages);
    }

    /**
     * Получает информацию о конкретном заказе ресторана по его идентификатору.
     *
     * @param orderId    идентификатор заказа
     * @param restaurant аутентифицированный ресторан
     * @return информация о заказе ресторана
     */
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public OrderRestaurantResponseDto getOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User restaurant) {
        Order order = orderRestaurantService.getOrder(orderId, restaurant);

        return orderMapper.toRestaurantResponse(order);
    }

    /**
     * Помечает заказ как в приготовлении.
     *
     * @param orderId    идентификатор заказа
     * @param restaurant аутентифицированный ресторан
     */
    @PatchMapping("/{orderId}/prepare")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void prepareOrder(@PathVariable Long orderId,
                             @AuthenticationPrincipal User restaurant) {
        orderRestaurantService.prepareOrder(orderId, restaurant);
    }

    /**
     * Помечает заказ как готовый.
     *
     * @param orderId    идентификатор заказа
     * @param restaurant аутентифицированный ресторан
     */
    @PatchMapping("/{orderId}/ready")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void readyOrder(@PathVariable Long orderId,
                           @AuthenticationPrincipal User restaurant) {
        orderRestaurantService.readyOrder(orderId, restaurant);
    }
}
