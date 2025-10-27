package naumen.project.service;

import naumen.project.dto.auth.RefreshRequestDto;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.TokenType;
import naumen.project.exception.WebException;
import naumen.project.props.AuthProps;
import naumen.project.repository.AuthTokenRepository;
import naumen.project.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Сервис с операциями с токенами в бд
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

    @Transactional(readOnly = true)
    public boolean isTokenAllowed(String token, TokenType type) {
        return authTokenRepository.existsByTokenAndType(token, type);
    }

    @Transactional
    public TokenResponseDto generateAndSave(User user) {
        AuthToken accessToken = generateAccessToken(user);
        AuthToken refreshToken = generateRefreshToken(user);
        return new TokenResponseDto(
                accessToken.getToken(),
                refreshToken.getToken()
        );
    }

    @Transactional
    public TokenResponseDto refresh(RefreshRequestDto request) {
        if (!jwtUtil.validateRefreshToken(request.refreshToken()) ||
            !isTokenAllowed(request.refreshToken(), TokenType.REFRESH)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Токен невалиден");
        }

        AuthToken currentRefreshToken = getToken(request.refreshToken(), TokenType.REFRESH);
        return generateAndSave(currentRefreshToken.getUser());
    }

    @Transactional
    public void logout(User user) {
        authTokenRepository.removeAllByUser(user);
    }

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

    private AuthToken getToken(String token, TokenType type) {
        return authTokenRepository.findByTokenAndType(token, type)
                .orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Токен не найден"));
    }
}
