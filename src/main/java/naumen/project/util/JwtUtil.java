package naumen.project.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import naumen.project.auth.AuthProps;
import naumen.project.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Утилитарный класс для работы с JWT токенами.
 * Обеспечивает генерацию, валидацию и извлечение данных из access и refresh токенов.
 *
 * @see AuthProps
 * @see User
 */
@Component
public class JwtUtil {
    private final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final AuthProps authProps;

    public JwtUtil(AuthProps authProps) {
        this.authProps = authProps;
    }

    /**
     * Генерирует access токен для пользователя.
     *
     * @param user пользователь для которого генерируется токен
     * @return сгенерированный access токен
     */
    public String generateAccessToken(User user) {
        return createToken(
                null,
                String.valueOf(user.getId()),
                getSignKey(authProps.getAccess().getToken()),
                authProps.getAccess().getLifetime()
        );
    }

    /**
     * Генерирует refresh токен для пользователя.
     *
     * @param user пользователь для которого генерируется токен
     * @return сгенерированный refresh токен
     */
    public String generateRefreshToken(User user) {
        return createToken(
                null,
                String.valueOf(user.getId()),
                getSignKey(authProps.getRefresh().getToken()),
                authProps.getRefresh().getLifetime()
        );
    }

    /**
     * Проверяет валидность access токена.
     *
     * @param token проверяемый токен
     * @return true если токен валиден
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, getSignKey(authProps.getAccess().getToken()));
    }

    /**
     * Проверяет валидность refresh токена.
     *
     * @param token проверяемый токен
     * @return true если токен валиден
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, getSignKey(authProps.getRefresh().getToken()));
    }

    /**
     * Извлекает claims из access токена.
     *
     * @param token access токен
     * @return claims токена
     */
    public Claims getAccessClaims(String token) {
        return getClaims(token, getSignKey(authProps.getAccess().getToken()));
    }

    /**
     * Извлекает claims из refresh токена.
     *
     * @param token refresh токен
     * @return claims токена
     */
    public Claims getRefreshClaims(String token) {
        return getClaims(token, getSignKey(authProps.getRefresh().getToken()));
    }

    /**
     * Извлекает идентификатор пользователя из access токена.
     *
     * @param token access токен
     * @return идентификатор пользователя
     */
    public Long extractAccessUserId(String token) {
        return Long.parseLong(getAccessClaims(token).getSubject());
    }

    /**
     * Получить ключ для подписи токена
     *
     * @param key ключ в виде строки
     * @return экзмеляр {@link SecretKey}
     */
    private SecretKey getSignKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    /**
     * Сгенерировать токен
     *
     * @param claims   claims для токена
     * @param subject  кому выдается токен (у нас это id пользователя)
     * @param signKey  ключ для подписи
     * @param lifetime время жизни токена в секундах
     * @return строка - токен
     */
    private String createToken(Map<String, Object> claims, String subject, SecretKey signKey, Long lifetime) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + lifetime * 1000))
                .signWith(signKey)
                .compact();
    }

    /**
     * Валидация токена
     *
     * @param token     сам токен в виде строки
     * @param verifyKey ключ для подписи
     * @return true - токен валиден, false - токен невалиден
     */
    private boolean validateToken(String token, SecretKey verifyKey) {
        try {
            getClaims(token, verifyKey);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT токен просрочен: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Подпись JWT токена недействительна: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT токен поврежден или имеет неверный формат: {}", e.getMessage());
        } catch (DecodingException e) {
            log.warn("Ошибка декодирования JWT токена: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Не удалось валидировать JWT токен: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Извлечь все claims из токена
     *
     * @param token     токен
     * @param verifyKey ключ для подписи
     * @return экземпляр {@link Claims}
     */
    private Claims getClaims(String token, SecretKey verifyKey) {
        return Jwts.parser()
                .verifyWith(verifyKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
