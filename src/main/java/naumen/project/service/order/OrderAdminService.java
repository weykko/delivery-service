package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
import naumen.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для административных операций с заказами.
 * Предоставляет методы для получения, обновления и удаления заказов.
 *
 * @see OrderService
 * @see UserService
 */
@Service
public class OrderAdminService {

    private final OrderService orderService;
    private final UserService userService;

    OrderAdminService(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Получение заказов по идентификатору клиента с поддержкой пагинации.
     *
     * @param clientId идентификатор клиента
     * @param pageable параметры пагинации
     * @return страница заказов клиента
     */
    public Page<Order> getOrders(Long clientId, Pageable pageable) {
        return orderService.getOrdersByClientId(clientId, pageable);
    }

    /**
     * Получение заказа по его идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return заказ
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderService.getById(orderId);
    }

    /**
     * Обновление информации о заказе.
     *
     * @param order заказ с обновленными данными
     */
    public void saveOrder(Order order) {
        orderService.save(order);
    }

    /**
     * Логическое удаление заказа по его идентификатору.
     * Заказ не может быть удален, если его статус - COMPLETED.
     *
     * @param orderId идентификатор заказа
     */
    public void deleteOrder(Long orderId) {
        Order order = orderService.getById(orderId)
                .orElseThrow(() -> new InvalidInputException("Не удалось удалить заказ. Заказ с id '%d' не найден",
                        orderId));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidInputException("Невозможно удалить завершенный заказ c id '%d'", orderId);
        }

        order.setStatus(OrderStatus.DELETED);

        orderService.save(order);
    }

    /**
     * Устанавливает курьера для заказа по его идентификатору.
     *
     * @param order     заказ
     * @param courierId идентификатор курьера
     */
    public void setCourierById(Order order, Long courierId) {
        User courier = userService.getUserById(courierId)
                .orElseThrow(() ->
                        new InvalidInputException("Не удалось назначить курьера: Пользователь с id '%d' не найден",
                                courierId));

        if (courier.getRole() != Role.COURIER) {
            throw new InvalidInputException("Пользователь с id '%d' не является курьером", courierId);
        }

        order.setCourier(courier);
    }
}
