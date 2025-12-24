package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.order.admin.OrderAdminResponseDto;
import naumen.project.dto.order.admin.OrderAdminShortResponseDto;
import naumen.project.dto.order.admin.OrderAdminUpdateRequestDto;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.entity.Order;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.InvalidInputException;
import naumen.project.mapper.OrderMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.service.order.OrderAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для управления заказами администраторами.
 * Предоставляет endpoints для операций с заказами.
 * Требует аутентификации с JWT токеном.
 *
 * @see OrderAdminService
 * @see OrderMapper
 * @see PageMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final OrderAdminService orderAdminService;
    private final OrderMapper orderMapper;
    private final PageMapper pageMapper;

    public AdminOrderController(OrderAdminService orderAdminService, OrderMapper orderMapper, PageMapper pageMapper) {
        this.orderAdminService = orderAdminService;
        this.orderMapper = orderMapper;
        this.pageMapper = pageMapper;
    }

    /**
     * Получает все заказы с возможностью фильтрации по клиенту и пагинацией.
     *
     * @param clientId идентификатор клиента для фильтрации (необязательно)
     * @param page     номер страницы
     * @param size     количество элементов на странице
     * @return страница с заказами
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public PagedResponseDto<OrderAdminShortResponseDto> getOrders(
            @RequestParam(required = false) Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderAdminShortResponseDto> orderPages = orderAdminService
                .getOrders(clientId, PageRequest.of(page, size))
                .map(orderMapper::toAdminShortResponse);

        return pageMapper.toOrderAdminResponse(orderPages);
    }

    /**
     * Получает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return данные заказа
     */
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public OrderAdminResponseDto getOrder(@PathVariable Long orderId) {
        Order order = orderAdminService.getOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с id '%d' не найден", orderId));

        return orderMapper.toAdminResponse(order);
    }

    /**
     * Обновляет информацию о заказе по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @param request данные для обновления заказа
     * @return обновленные данные заказа
     */
    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public OrderAdminResponseDto updateOrder(@PathVariable Long orderId,
                                             @RequestBody @Valid OrderAdminUpdateRequestDto request) {
        Order order = orderAdminService.getOrderById(orderId)
                .orElseThrow(() ->
                        new InvalidInputException("Не удалось обновить заказ, причина: Заказ с id '%d' не найден",
                                orderId));

        Optional.ofNullable(request.status()).ifPresent(order::setStatus);
        Optional.ofNullable(request.totalPrice()).ifPresent(order::setTotalPrice);
        Optional.ofNullable(request.courierId()).ifPresent(courierId ->
                orderAdminService.setCourierById(order, courierId));

        orderAdminService.saveOrder(order);

        return orderMapper.toAdminResponse(order);
    }

    /**
     * Удаляет заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     */
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteOrder(@PathVariable Long orderId) {
        orderAdminService.deleteOrder(orderId);
    }
}
