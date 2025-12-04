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
public class AuthToken extends IdEntity {
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

    /**
     * Конструктор с параметрами
     *
     * @param token    JWT токен
     * @param type     тип токена
     * @param expireAt время истечения срока действия токена
     * @param user     пользователь, которому принадлежит токен
     */
    public AuthToken(String token, TokenType type, Instant expireAt, User user) {
        this.token = token;
        this.type = type;
        this.expireAt = expireAt;
        this.user = user;
    }

    /**
     * Конструктор по умолчанию для JPA
     */
    public AuthToken() {
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
}
