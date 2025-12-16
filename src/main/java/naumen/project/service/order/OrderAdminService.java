package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.InvalidInputException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис для административных операций с заказами.
 * Предоставляет методы для получения, обновления и удаления заказов.
 *
 * @see OrderService
 */
@Service
public class OrderAdminService {

    private final OrderService orderService;

    OrderAdminService(OrderService orderService) {
        this.orderService = orderService;
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
    public Order getOrderById(Long orderId) {
        return orderService.getById(orderId);
    }

    /**
     * Обновление информации о заказе.
     *
     * @param order заказ с обновленными данными
     * @return обновленный заказ
     */
    public Order updateOrder(Order order) {
        return orderService.save(order);
    }

    /**
     * Логическое удаление заказа по его идентификатору.
     * Заказ не может быть удален, если его статус - COMPLETED.
     *
     * @param orderId идентификатор заказа
     * @return обновленный заказ с пометкой об удалении
     */
    public Order deleteOrder(Long orderId) {
        Order order = orderService.getById(orderId);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidInputException("Невозможно удалить завершенный заказ c id '%d'", orderId);
        }

        order.setStatus(OrderStatus.DELETED);

        return orderService.save(order);
    }
}
