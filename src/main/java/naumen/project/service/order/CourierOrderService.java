package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с заказами со стороны курьеров
 */
@Service
public class CourierOrderService {

    private final OrderService orderService;

    CourierOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Получение доступных заказов для курьеров
     *
     * @param pageable параметры пагинации
     * @return страница с доступными заказами
     */
    public Page<Order> getAvailableOrders(Pageable pageable) {
        return orderService.getAvailableOrdersForCourier(pageable);
    }

    /**
     * Получение активных заказов курьера
     *
     * @param courier курьер
     * @return список активных заказов курьера
     */
    public List<Order> getActiveOrders(User courier) {
        return orderService.getActiveOrdersByCourier(courier);
    }

    /**
     * Прием заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void acceptOrder(Long orderId, User courier) {
        Order order = orderService.getById(orderId)
                .orElseThrow(() ->
                        new InvalidInputException("Нельзя принять заказ, причина: Заказ с id '%d' не найден",
                                orderId));

        if (order.getCourier() != null) {
            throw new InvalidInputException("Заказ с id '%d' уже принят курьером", orderId);
        }

        order.setCourier(courier);
        orderService.save(order);
    }

    /**
     * Забор заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void pickUpOrder(Long orderId, User courier) {
        Order order = orderService.getById(orderId)
                .orElseThrow(() ->
                        new InvalidInputException("Нельзя забрать заказ, причина: Заказ с id '%d' не найден",
                                orderId));

        assertBelongsToCourier(order, courier);

        switch (order.getStatus()) {
            case PREPARED -> order.setStatus(OrderStatus.DELIVERING);
            case DELIVERING -> throw new InvalidInputException("Заказ с id '%d' уже доставляется", orderId);
            case COMPLETED -> throw new InvalidInputException("Заказ с id '%d' уже доставлен", orderId);
            default -> throw new InvalidInputException("Заказ с id '%d' ещё не готов", orderId);
        }

        orderService.save(order);
    }

    /**
     * Доставка заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void deliverOrder(Long orderId, User courier) {
        Order order = orderService.getById(orderId)
                .orElseThrow(() ->
                        new InvalidInputException("Нельзя доставить заказ, причина: Заказ с id '%d' не найден",
                                orderId));

        assertBelongsToCourier(order, courier);

        switch (order.getStatus()) {
            case DELIVERING -> order.setStatus(OrderStatus.COMPLETED);
            case COMPLETED -> throw new InvalidInputException("Заказ с id '%d' уже доставлен", orderId);
            default -> throw new InvalidInputException("Заказ с id '%d' ещё не доставляется", orderId);
        }

        orderService.save(order);
    }

    /**
     * Проверяет, принадлежит ли заказ указанному курьеру
     *
     * @param order   заказ
     * @param courier курьер
     */
    private void assertBelongsToCourier(Order order, User courier) {
        if (order.getCourier() == null
            || !order.getCourier().getId().equals(courier.getId())) {
            throw new PermissionCheckFailedException("Заказ с id '%d' не принадлежит вам", order.getId());
        }
    }

}
