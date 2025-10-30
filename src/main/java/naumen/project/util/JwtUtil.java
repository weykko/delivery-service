package naumen.project.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import naumen.project.entity.User;
import naumen.project.props.AuthProps;
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

    private SecretKey getSignKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    private String createToken(Map<String, Object> claims, String subject, SecretKey signKey, Long lifetime) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + lifetime * 1000))
                .signWith(signKey)
                .compact();
    }

    private boolean validateToken(String token, SecretKey verifyKey) {
        try {
            getClaims(token, verifyKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token, SecretKey verifyKey) {
        return Jwts.parser()
                .verifyWith(verifyKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
