package naumen.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Сущность позиции меню ресторана.
 * Содержит информацию о блюде и ссылку на ресторан-владельца.
 *
 * @see User
 */
@Entity
@Table(name = "menu_item")
public class MenuItem {
    /**
     * Уникальный идентификатор позиции меню
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название блюда
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Описание блюда
     */
    @Column(name = "description")
    private String description;

    /**
     * Цена блюда
     */
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Ресторан, которому принадлежит позиция меню
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private User restaurant;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public User getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(User restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MenuItem menuItem)) return false;

        return id.equals(menuItem.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
