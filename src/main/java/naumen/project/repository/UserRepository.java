package naumen.project.repository;

import naumen.project.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO слой для пользователя
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
