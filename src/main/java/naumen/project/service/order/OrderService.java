package naumen.project.service.order;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Общие методы по работе с заказами.
 * Это внутренний сервис, который далее используют более "высокоуровневые" сервисы.
 * Доступ к классу и его методам только в пакете Order
 *
 * @see ClientOrderService
 * @see CourierOrderService
 * @see RestaurantOrderService
 */
@Service
class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Получение заказа по идентификатору
     *
     * @param id идентификатор заказа
     * @return заказ
     */
    Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Сохранение заказа
     *
     * @param order заказ
     * @return сохраненный заказ
     */
    Order save(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Получение заказов по клиенту
     *
     * @param client клиент
     * @return список заказов клиента
     */
    List<Order> getByClient(User client) {
        return orderRepository.findOrdersByClient(client);
    }

    /**
     * Получение доступных заказов для курьеров
     *
     * @param pageable параметры пагинации
     * @return страница с доступными заказами
     */
    Page<Order> getAvailableOrdersForCourier(Pageable pageable) {
        return orderRepository.findAvailableOrdersForCourier(pageable);
    }

    /**
     * Получение активных заказов курьера
     *
     * @param courier курьер
     * @return список активных заказов курьера
     */
    List<Order> getActiveOrdersByCourier(User courier) {
        return orderRepository.findActiveOrdersByCourier(courier);
    }

    /**
     * Получение активных заказов ресторана
     *
     * @param restaurant ресторан
     * @param pageable   параметры пагинации
     * @return страница с активными заказами ресторана
     */
    Page<Order> getActiveOrdersByRestaurant(User restaurant, Pageable pageable) {
        return orderRepository.findActiveOrdersByRestaurant(restaurant, pageable);
    }

    /**
     * Получение заказов по идентификатору клиента с пагинацией
     *
     * @param clientId идентификатор клиента
     * @param pageable параметры пагинации
     * @return страница с заказами клиента
     */
    Page<Order> getOrdersByClientId(Long clientId, Pageable pageable) {
        if (clientId == null) {
            return orderRepository.findAll(pageable);
        }

        return orderRepository.findOrdersByClientId(clientId, pageable);
    }
}
