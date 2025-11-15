package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.OrderItem;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.WebException;
import naumen.project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для работы с заказами со стороны клиентов
 */
@Service
public class OrderClientService {

    private final OrderService orderService;
    private final UserService userService;

    public OrderClientService(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Оформление заказа клиентом.
     * Все позиции заказа должны принадлежать указанному ресторану
     *
     * @param restaurantId    id ресторана
     * @param orderItems      заказанное меню
     * @param deliveryAddress адрес доставки
     * @param client          заказчик - клиент
     * @return заказ
     */
    public Order createOrder(Long restaurantId, List<OrderItem> orderItems,
                             String deliveryAddress, User client) {
        boolean allItemsBelongToRestaurant = orderItems.stream()
                .allMatch(item -> item.getMenuItem().getRestaurant().getId().equals(restaurantId));

        if (!allItemsBelongToRestaurant) {
            throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Все позиции заказа должны принадлежать ресторану с id '%d'",
                    restaurantId);
        }

        User restaurant = userService.getById(restaurantId);

        Order order = new Order();
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.CREATED);
        order.setItems(orderItems);
        order.setTotalPrice(
                orderItems.stream()
                        .map(OrderItem::getItemPrice)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );
        order.setRestaurant(restaurant);
        order.setClient(client);

        orderItems.forEach(item -> item.setOrder(order));

        return orderService.save(order);
    }

    /**
     * Получение заказов клиента
     *
     * @param client клиент
     * @return страница с заказами клиента
     */
    public List<Order> getOrders(User client) {
        return orderService.getByClient(client);
    }

    /**
     * Получить информацию по заказу. Для клиента. Проверяется, что заказ принадлежит клиенту
     *
     * @param orderId id заказа
     * @param client  текущий клиент
     * @return заказ
     */
    public Order getOrder(Long orderId, User client) {
        Order order = orderService.getById(orderId);
        assertBelongsToClient(order, client);
        return order;
    }

    /**
     * Удалить заказ. Т.е. перевести в статус DELETED
     *
     * @param orderId id заказа
     * @param client  текущий клиент
     */
    public void deleteOrder(Long orderId, User client) {
        Order order = orderService.getById(orderId);

        assertBelongsToClient(order, client);
        if (order.getStatus() != OrderStatus.CREATED ||
            order.getCourier() != null) {
            throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже принят в работу",
                    orderId);
        }

        order.setStatus(OrderStatus.DELETED);
        orderService.save(order);
    }

    /**
     * Проверяет, принадлежит ли заказ указанному клиенту
     *
     * @param order  заказ
     * @param client клиент
     */
    private void assertBelongsToClient(Order order, User client) {
        if (!order.getClient().getId().equals(client.getId())) {
            throw new WebException(
                    HttpStatus.FORBIDDEN,
                    "Заказ c id '%d' не принадлежит вам",
                    order.getId());
        }
    }
}
