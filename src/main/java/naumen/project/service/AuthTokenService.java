package naumen.project.service;

import naumen.project.auth.AuthProps;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.TokenType;
import naumen.project.exception.WebException;
import naumen.project.repository.AuthTokenRepository;
import naumen.project.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Сервис для управления JWT токенами в базе данных.
 * Отвечает за генерацию, валидацию и отзыв токенов.
 *
 * @see AuthTokenRepository
 * @see JwtUtil
 */
@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;
    private final JwtUtil jwtUtil;
    private final AuthProps authProps;

    public AuthTokenService(
            AuthTokenRepository authTokenRepository,
            JwtUtil jwtUtil,
            AuthProps authProps
    ) {
        this.authTokenRepository = authTokenRepository;
        this.jwtUtil = jwtUtil;
        this.authProps = authProps;
    }

    /**
     * Проверяет, разрешен ли токен к использованию.
     *
     * @param token проверяемый токен
     * @param type  тип токена
     * @return true если токен активен и разрешен
     */
    @Transactional(readOnly = true)
    public boolean isTokenAllowed(String token, TokenType type) {
        return authTokenRepository.existsByTokenAndType(token, type);
    }

    /**
     * Генерирует и сохраняет новую пару токенов для пользователя.
     *
     * @param user пользователь для которого генерируются токены
     * @return сгенерированные токены
     */
    @Transactional
    public TokenResponseDto generateAndSave(User user) {
        AuthToken accessToken = generateAccessToken(user);
        AuthToken refreshToken = generateRefreshToken(user);
        return new TokenResponseDto(
                accessToken.getToken(),
                refreshToken.getToken()
        );
    }

    /**
     * Обновляет пару токенов по валидному refresh токену.
     *
     * @param refreshToken refresh токен
     * @return новая пара токенов
     */
    @Transactional
    public TokenResponseDto refresh(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken) ||
            !isTokenAllowed(refreshToken, TokenType.REFRESH)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Токен невалиден");
        }

        AuthToken currentRefreshToken = getToken(refreshToken, TokenType.REFRESH);
        return generateAndSave(currentRefreshToken.getUser());
    }

    /**
     * Выполняет выход пользователя, удаляя все его токены.
     *
     * @param user пользователь выполняющий выход
     */
    @Transactional
    public void logout(User user) {
        authTokenRepository.removeAllByUser(user);
    }

    /**
     * Генерирует access токен.
     *
     * @param user пользователь
     * @return access токен
     */
    private AuthToken generateAccessToken(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        AuthToken accessAuthToken = new AuthToken();
        accessAuthToken.setToken(accessToken);
        accessAuthToken.setType(TokenType.ACCESS);
        accessAuthToken.setExpireAt(Instant.now().plusSeconds(authProps.getAccess().getLifetime()));
        accessAuthToken.setUser(user);

        authTokenRepository.save(accessAuthToken);

        return accessAuthToken;
    }

    /**
     * Генерирует refresh токен.
     *
     * @param user пользователь
     * @return refresh токен
     */
    private AuthToken generateRefreshToken(User user) {
        String refreshToken = jwtUtil.generateRefreshToken(user);
        AuthToken refreshAuthToken = new AuthToken();
        refreshAuthToken.setToken(refreshToken);
        refreshAuthToken.setType(TokenType.REFRESH);
        refreshAuthToken.setExpireAt(Instant.now().plusSeconds(authProps.getRefresh().getLifetime()));
        refreshAuthToken.setUser(user);

        authTokenRepository.save(refreshAuthToken);

        return refreshAuthToken;
    }

    /**
     * Получает токен определенного типа из базы данных.
     *
     * @param token токен
     * @param type  тип токена
     * @return токен из базы данных
     */
    private AuthToken getToken(String token, TokenType type) {
        return authTokenRepository.findByTokenAndType(token, type)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Токен не найден"));
    }
}
