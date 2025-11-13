package naumen.project.service;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import naumen.project.entity.enums.OrderStatus;
import naumen.project.exception.WebException;
import naumen.project.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Получение доступных заказов для курьеров
     *
     * @param pageable параметры пагинации
     * @return страница с доступными заказами
     */
    public Page<Order> getAvailableOrders(Pageable pageable) {
        return orderRepository.findAvailableOrders(pageable);
    }

    /**
     * Получение активных заказов курьера
     *
     * @param courier курьер
     * @return список активных заказов курьера
     */
    public List<Order> getActiveOrdersByCourier(User courier) {
        return orderRepository.findActiveOrdersByCourier(courier);
    }

    /**
     * Прием заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void acceptOrderByCourier(Long orderId, User courier) {
        Order order = getOrderById(orderId);

        if (order.getCourier() != null) {
            throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже принят курьером",
                    orderId);
        }

        order.setCourier(courier);
        orderRepository.save(order);
    }

    /**
     * Забор заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void pickUpOrderByCourier(Long orderId, User courier) {
        Order order = getOrderById(orderId);

        assertBelongsToCourier(order, courier);

        switch (order.getStatus()) {
            case PREPARED -> order.setStatus(OrderStatus.DELIVERING);
            case DELIVERING -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже доставляется",
                    orderId);
            case COMPLETED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже доставлен",
                    orderId);
            default -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' ещё не готов",
                    orderId
            );
        }

        orderRepository.save(order);
    }

    /**
     * Доставка заказа курьером
     *
     * @param orderId идентификатор заказа
     * @param courier курьер
     */
    public void deliverOrderByCourier(Long orderId, User courier) {
        Order order = getOrderById(orderId);

        assertBelongsToCourier(order, courier);

        switch (order.getStatus()) {
            case DELIVERING -> order.setStatus(OrderStatus.COMPLETED);
            case COMPLETED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже доставлен",
                    orderId);
            default -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' ещё не доставляется",
                    orderId
            );
        }

        orderRepository.save(order);
    }

    /**
     * Получение активных заказов ресторана
     *
     * @param restaurant ресторан
     * @param pageable   параметры пагинации
     * @return страница с активными заказами ресторана
     */
    public Page<Order> getActiveOrdersByRestaurant(User restaurant, Pageable pageable) {
        return orderRepository.findActiveOrdersByRestaurant(restaurant, pageable);
    }

    /**
     * Получение заказа ресторана по идентификатору
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     * @return заказ
     */
    public Order getOrderByRestaurant(Long orderId, User restaurant) {
        Order order = getOrderById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        return order;
    }

    /**
     * Пометка заказа как в приготовлении рестораном
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     */
    public void prepareOrderByRestaurant(Long orderId, User restaurant) {
        Order order = getOrderById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        switch (order.getStatus()) {
            case CREATED -> order.setStatus(OrderStatus.ACCEPTED);
            case ACCEPTED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже готовиться",
                    orderId);
            case PREPARED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже приготовлен",
                    orderId);
            default -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже был отдан курьеру",
                    orderId
            );
        }

        orderRepository.save(order);
    }

    /**
     * Пометка заказа как готового рестораном
     *
     * @param orderId    идентификатор заказа
     * @param restaurant ресторан
     */
    public void readyOrderByRestaurant(Long orderId, User restaurant) {
        Order order = getOrderById(orderId);

        assertBelongsToRestaurant(order, restaurant);

        switch (order.getStatus()) {
            case ACCEPTED -> order.setStatus(OrderStatus.PREPARED);
            case CREATED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' еще не начал готовиться",
                    orderId);
            case PREPARED -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже приготовлен",
                    orderId);
            default -> throw new WebException(
                    HttpStatus.BAD_REQUEST,
                    "Заказ с id '%d' уже был отдан курьеру",
                    orderId
            );
        }

        orderRepository.save(order);
    }

    /**
     * Получение заказа по идентификатору
     *
     * @param id идентификатор заказа
     * @return заказ
     */
    private Order getOrderById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new WebException(
                        HttpStatus.NOT_FOUND,
                        "Заказ с id '%d' не найден",
                        id));
    }

    /**
     * Проверяет, принадлежит ли заказ указанному курьеру
     *
     * @param order   заказ
     * @param courier курьер
     */
    private void assertBelongsToCourier(Order order, User courier) {
        if (order.getCourier() == null ||
                !order.getCourier().getId().equals(courier.getId())) {
            throw new WebException(
                    HttpStatus.FORBIDDEN,
                    "Заказ с id '%d' не принадлежит вам",
                    order.getId());
        }
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
            throw new WebException(
                    HttpStatus.FORBIDDEN,
                    "Заказ с id '%d' не принадлежит вашему ресторану",
                    order.getId());
        }
    }
}
