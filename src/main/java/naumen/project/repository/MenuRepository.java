package naumen.project.repository;

import naumen.project.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    @Query(value = "SELECT * FROM menu_item m WHERE " +
            "(:restaurantId IS NULL OR m.restaurant_id = :restaurantId) AND " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))",
            nativeQuery = true)
    Page<MenuItem> findByRestaurantIdAndTitle(
            @Param("restaurantId") Long restaurantId,
            @Param("title") String title,
            Pageable pageable);
}
