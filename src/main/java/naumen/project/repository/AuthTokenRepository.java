package naumen.project.repository;

import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.TokenType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

/**
 * DAO слой для {@link AuthToken}
 */
public interface AuthTokenRepository extends CrudRepository<AuthToken, Long> {
    boolean existsByTokenAndType(String token, TokenType type);

    Optional<AuthToken> findByTokenAndType(String token, TokenType type);

    void removeAllByUser(User user);

    @Query("""
            DELETE FROM AuthToken a WHERE :now_time > a.expireAt
            """)
    @Modifying
    void removeAllExpired(@Param("now_time") Instant now);
}
