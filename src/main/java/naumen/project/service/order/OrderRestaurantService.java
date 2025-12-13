package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с заказами со стороны ресторанов
 */
@Service
public class OrderRestaurantService {

    private final OrderService orderService;

    public OrderRestaurantService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Получение активных заказов ресторана
     *
     * @param restaurant ресторан
     * @param pageable   параметры пагинации
     * @return страница с активными заказами ресторана
     */
    public Page<Order> getActiveOrders(User restaurant, Pageable pageable) {
        return orderService.getActiveOrdersByRestaurant(restaurant, pageable);
    }

    /**
     * Получение заказа ресторана по идентификатору
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     * @return заказ
     */
    public Order getOrder(Long orderId, User restaurant) {
        Order order = orderService.getById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        return order;
    }

    /**
     * Пометка заказа как в приготовлении рестораном
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     */
    public void prepareOrder(Long orderId, User restaurant) {
        Order order = orderService.getById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        switch (order.getStatus()) {
            case CREATED -> order.setStatus(OrderStatus.ACCEPTED);
            case ACCEPTED -> throw new InvalidInputException("Заказ с id '%d' уже готовиться", orderId);
            case PREPARED -> throw new InvalidInputException("Заказ с id '%d' уже приготовлен", orderId);
            default -> throw new InvalidInputException("Заказ с id '%d' уже был отдан курьеру", orderId
            );
        }

        orderService.save(order);
    }

    /**
     * Пометка заказа как готового рестораном
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     */
    public void readyOrder(Long orderId, User restaurant) {
        Order order = orderService.getById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        switch (order.getStatus()) {
            case ACCEPTED -> order.setStatus(OrderStatus.PREPARED);
            case CREATED -> throw new InvalidInputException("Заказ с id '%d' еще не начал готовиться", orderId);
            case PREPARED -> throw new InvalidInputException("Заказ с id '%d' уже приготовлен", orderId);
            default -> throw new InvalidInputException("Заказ с id '%d' уже был отдан курьеру", orderId
            );
        }

        orderService.save(order);
    }

    /**
     * Проверяет, принадлежит ли заказ указанному ресторану
     *
     * @param order      заказ
     * @param restaurant ресторан
     */
    private void assertBelongsToRestaurant(Order order, User restaurant) {
        if (order.getRestaurant() == null ||
            !order.getRestaurant().getId().equals(restaurant.getId())) {
            throw new PermissionCheckFailedException("Заказ с id '%d' не принадлежит вашему ресторану", order.getId());
        }
    }
}
