package naumen.project.repository;

import naumen.project.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DAO слой для {@link User}
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return Optional с найденным пользователем
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email проверяемый email
     * @return true если пользователь с таким email существует
     */
    boolean existsByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным телефоном.
     *
     * @param phone проверяемый телефон
     * @return true если пользователь с таким телефоном существует
     */
    boolean existsByPhone(String phone);

    /**
     * Количество пользователей с номером телефона
     * @param phone проверяемый телефон
     * @return число пользователей
     */
    int countByPhone(String phone);
}
