package naumen.project.entity;

import jakarta.persistence.*;
import naumen.project.entity.enums.TokenType;

import java.time.Instant;

/**
 * Сущность JWT токена в базе данных.
 * Хранит информацию о выданных токенах для управления сессиями.
 *
 * @see TokenType
 * @see User
 */
@Entity
@Table(name = "auth_token")
public class AuthToken {

    /**
     * Уникальный идентификатор токена
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * JWT токен
     */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /**
     * Тип токена (access, refresh и т.д.)
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType type;

    /**
     * Время истечения срока действия токена
     */
    @Column(name = "expire_at", nullable = false)
    private Instant expireAt;

    /**
     * Пользователь, которому принадлежит токен
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AuthToken authToken)) return false;

        return token.equals(authToken.token) && type == authToken.type;
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
