package naumen.project.repository;

import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.TokenType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * DAO слой для {@link AuthToken}
 */
@Repository
public interface AuthTokenRepository extends CrudRepository<AuthToken, Long> {

    /**
     * Проверяет существование токена определенного типа.
     *
     * @param token значение токена
     * @param type тип токена
     * @return true если токен существует и активен
     */
    boolean existsByTokenAndType(String token, TokenType type);

    /**
     * Находит токен по его значению и типу.
     *
     * @param token значение токена
     * @param type тип токена
     * @return Optional с найденным токеном
     */
    Optional<AuthToken> findByTokenAndType(String token, TokenType type);

    /**
     * Удаляет все токены пользователя.
     *
     * @param user пользователь, чьи токены нужно удалить
     */
    void removeAllByUser(User user);

    /**
     * Удаляет все истекшие токены из базы данных.
     *
     * @param now текущее время для сравнения с сроком действия
     */
    @Query("""
            DELETE FROM AuthToken a WHERE :now_time > a.expireAt
            """)
    @Modifying
    void removeAllExpired(@Param("now_time") Instant now);
}
