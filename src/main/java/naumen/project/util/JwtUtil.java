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
 * Утильные методы по работе с токенами
 */
@Component
public class JwtUtil {

    private final AuthProps authProps;

    public JwtUtil(AuthProps authProps) {
        this.authProps = authProps;
    }

    public String generateAccessToken(User user) {
        return createToken(
                null,
                String.valueOf(user.getId()),
                getSignKey(authProps.getAccess().getToken()),
                authProps.getAccess().getLifetime()
        );
    }

    public String generateRefreshToken(User user) {
        return createToken(
                null,
                String.valueOf(user.getId()),
                getSignKey(authProps.getRefresh().getToken()),
                authProps.getRefresh().getLifetime()
        );
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, getSignKey(authProps.getAccess().getToken()));
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, getSignKey(authProps.getRefresh().getToken()));
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, getSignKey(authProps.getAccess().getToken()));
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, getSignKey(authProps.getRefresh().getToken()));
    }

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
