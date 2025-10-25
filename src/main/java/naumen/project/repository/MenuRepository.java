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

    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<MenuItem> findByRestaurantIdAndTitleContainingIgnoreCase(Long restaurantId, String title, Pageable pageable);

    default Page<MenuItem> findByRestaurantIdAndTitle(Long restaurantId, String title, Pageable pageable) {
        if (title == null || title.trim().isEmpty()) {
            return findByRestaurantId(restaurantId, pageable);
        }
        else {
            return findByRestaurantIdAndTitleContainingIgnoreCase(restaurantId, title, pageable);
        }
    }

/*    Старая реализация, которая не проходила, так как hibernate делал LOWER(NULL) забивая на условие до этого

        @Query("SELECT m FROM MenuItem m WHERE " +
            "(:restaurantId IS NULL OR m.restaurant.id = :restaurantId) AND " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<MenuItem> findByRestaurantIdAndTitle(
            @Param("restaurantId") Long restaurantId,
            @Param("title") String title,
            Pageable pageable);*/
}
