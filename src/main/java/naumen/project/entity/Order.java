package naumen.project.entity;

import jakarta.persistence.*;
import naumen.project.entity.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Сущность заказа.
 */
@Entity
@Table(name = "\"order\"")
public class Order extends IdEntity {

    /**
     * Адрес для доставки
     */
    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    /**
     * Статус заказа
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    /**
     * Состав заказа
     */
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<OrderItem> items;

    /**
     * Общая стоимость заказа
     */
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    /**
     * Ресторан, у которого заказ
     */
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private User restaurant;

    /**
     * Клиент, тот, кто заказал
     */
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    /**
     * Курьер для данного заказа
     */
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private User courier;

    /**
     * Дата создания заказа
     */
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Конструктор для создания заказа.
     *
     * @param deliveryAddress адрес доставки
     * @param status          статус заказа
     * @param items           состав заказа
     * @param totalPrice      общая стоимость заказа
     * @param restaurant      ресторан, у которого заказ
     * @param client          клиент, тот, кто заказал
     */
    public Order(String deliveryAddress, OrderStatus status, List<OrderItem> items,
                 BigDecimal totalPrice, User restaurant, User client) {
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.items = items;
        this.totalPrice = totalPrice;
        this.restaurant = restaurant;
        this.client = client;
    }

    /**
     * Пустой конструктор для JPA.
     */
    public Order() {

    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(User restaurant) {
        this.restaurant = restaurant;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getCourier() {
        return courier;
    }

    public void setCourier(User courier) {
        this.courier = courier;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
