package naumen.project.repository;

import naumen.project.entity.Order;
import naumen.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO слой для {@link Order}
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Поиск доступных заказов для курьеров
     *
     * @param pageable параметры пагинации
     * @return страница с доступными заказами
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.courier IS NULL " +
            "AND o.status IN ('CREATED', 'ACCEPTED', 'PREPARED')")
    Page<Order> findAvailableOrders(Pageable pageable);

    /**
     * Поиск активных заказов курьера
     *
     * @param courier курьер
     * @return список активных заказов курьера
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.courier = :courier " +
            "AND o.status != 'COMPLETED'")
    List<Order> findActiveOrdersByCourier(User courier);

    /**
     * Поиск активных заказов ресторана
     *
     * @param restaurant ресторан
     * @return список активных заказов ресторана
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.restaurant = :restaurant " +
            "AND o.status IN ('CREATED', 'ACCEPTED', 'PREPARED')")
    Page<Order> findActiveOrdersByRestaurant(User restaurant, Pageable pageable);

}
