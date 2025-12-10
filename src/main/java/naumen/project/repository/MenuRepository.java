package naumen.project.repository;

import naumen.project.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO слой для {@link MenuItem}
 */
@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Находит страницу позиций меню с фильтрацией по ресторану, названию и пагинацией.
     *
     * @param restaurantId идентификатор ресторана (опционально)
     * @param title текст для поиска в названии (опционально)
     * @param pageable параметры пагинации
     * @return страница с найденными позициями меню
     */
    @Query(value = "SELECT * FROM menu_item m WHERE " +
            "(:restaurantId IS NULL OR m.restaurant_id = :restaurantId) AND " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))",
            nativeQuery = true)
    Page<MenuItem> findByRestaurantIdAndTitle(
            @Param("restaurantId") Long restaurantId,
            @Param("title") String title,
            Pageable pageable);
}
