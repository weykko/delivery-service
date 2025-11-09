package naumen.project.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Пункт меню в составе заказа
 *
 * @see Order
 */
@Entity
@Table(name = "order_item")
public class OrderItem extends IdEntity {

    /**
     * Количество штук блюда
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Зафиксированная стоимость выбранного блюда на момент заказа
     */
    @Column(name = "item_price", nullable = false)
    private BigDecimal itemPrice;

    /**
     * Заказанное блюдо
     */
    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    /**
     * К какому заказу относится
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
