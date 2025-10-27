package naumen.project.repository;

import naumen.project.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * DAO слой для пользователя
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
