package naumen.project.service;

import naumen.project.auth.AuthProps;
import naumen.project.auth.JwtUtil;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.entity.enums.TokenType;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.InvalidInputException;
import naumen.project.repository.AuthTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

/**
 * Модульные тесты для {@link AuthTokenService}
 */
@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthProps authProps;

    @Mock
    private AuthProps.TokenProps accessTokenConfig;

    @Mock
    private AuthProps.TokenProps refreshTokenConfig;

    @InjectMocks
    private AuthTokenService authTokenService;

    private final User testUser = createTestUser();
    private final AuthToken testRefreshToken = createRefreshToken(testUser);

    /**
     * Тестирование проверки допустимости токена
     */
    @Test
    void isTokenAllowedWithValidTokenShouldReturnTrue() {
        String accessToken = "access-token-123";

        Mockito.when(authTokenRepository.existsByTokenAndType(accessToken, TokenType.ACCESS))
                .thenReturn(true);

        boolean result = authTokenService.isTokenAllowed(accessToken, TokenType.ACCESS);

        Assertions.assertTrue(result);
        Mockito.verify(authTokenRepository).existsByTokenAndType(accessToken, TokenType.ACCESS);
    }

    /**
     * Тестирование проверки допустимости токена с невалидным токеном
     */
    @Test
    void isTokenAllowedWithInvalidTokenShouldReturnFalse() {
        String accessToken = "invalid-token";

        Mockito.when(authTokenRepository.existsByTokenAndType(accessToken, TokenType.ACCESS))
                .thenReturn(false);

        boolean result = authTokenService.isTokenAllowed(accessToken, TokenType.ACCESS);

        Assertions.assertFalse(result);
        Mockito.verify(authTokenRepository).existsByTokenAndType(accessToken, TokenType.ACCESS);
    }

    /**
     * Тестирование генерации и сохранения токенов
     */
    @Test
    void generateAndSaveWithValidUserShouldGenerateAndSaveTokens() {
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        Mockito.when(authProps.getAccess()).thenReturn(accessTokenConfig);
        Mockito.when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        Mockito.when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        Mockito.when(refreshTokenConfig.getLifetime()).thenReturn(86400L);

        Mockito.when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);
        Mockito.when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);
        Mockito.when(authTokenRepository.save(Mockito.any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponseDto result = authTokenService.generateAndSave(testUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(accessToken, result.accessToken());
        Assertions.assertEquals(refreshToken, result.refreshToken());

        Mockito.verify(jwtUtil).generateAccessToken(testUser);
        Mockito.verify(jwtUtil).generateRefreshToken(testUser);
        Mockito.verify(authTokenRepository, Mockito.times(2)).save(Mockito.any(AuthToken.class));
        Mockito.verify(authProps).getAccess();
        Mockito.verify(authProps).getRefresh();
    }

    /**
     * Тестирование обновления токенов с валидным рефреш токеном
     */
    @Test
    void refreshWithValidRefreshTokenShouldReturnNewTokens() {
        TokenResponseDto newTokens = new TokenResponseDto("new-access", "new-refresh");

        Mockito.when(jwtUtil.validateRefreshToken(testRefreshToken.getToken())).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(testRefreshToken.getToken(), TokenType.REFRESH))
                .thenReturn(true);
        Mockito.when(authTokenRepository.findByTokenAndType(testRefreshToken.getToken(), TokenType.REFRESH))
                .thenReturn(Optional.of(testRefreshToken));

        AuthTokenService spyService = Mockito.spy(authTokenService);
        Mockito.doReturn(newTokens).when(spyService).generateAndSave(testUser);

        TokenResponseDto result = spyService.refresh(testRefreshToken.getToken());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(newTokens, result);
        Mockito.verify(jwtUtil).validateRefreshToken(testRefreshToken.getToken());
        Mockito.verify(authTokenRepository).existsByTokenAndType(testRefreshToken.getToken(), TokenType.REFRESH);
        Mockito.verify(authTokenRepository).findByTokenAndType(testRefreshToken.getToken(), TokenType.REFRESH);
        Mockito.verify(spyService).generateAndSave(testUser);
    }

    /**
     * Тестирование обновления токенов с невалидным рефреш токеном
     */
    @Test
    void refreshWithInvalidJwtTokenShouldThrowException() {
        String refreshToken = "invalid-refresh-token";

        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен невалиден", exception.getMessage());
        Mockito.verify(jwtUtil).validateRefreshToken(refreshToken);
        Mockito.verify(authTokenRepository, Mockito.never()).existsByTokenAndType(Mockito.any(), Mockito.any());
    }

    /**
     * Тестирование обновления токенов с отсутствующим в базе рефреш токеном
     */
    @Test
    void refreshWithTokenNotInDatabaseShouldThrowException() {
        String refreshToken = "not-in-db-token";

        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(false);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен невалиден", exception.getMessage());
        Mockito.verify(jwtUtil).validateRefreshToken(refreshToken);
        Mockito.verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        Mockito.verify(authTokenRepository, Mockito.never()).findByTokenAndType(Mockito.any(), Mockito.any());
    }

    /**
     * Тестирование обновления токенов когда токен не найден в БД при поиске
     */
    @Test
    void refreshWithNonExistingTokenShouldThrowException() {
        String refreshToken = "refresh-token";

        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        Mockito.when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен не найден", exception.getMessage());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя
     */
    private User createTestUser() {
        User user = new User(
                "test@example.com",
                "Test User",
                "+79991234567",
                Role.CLIENT,
                "Пушкина 17"
        );
        user.setId(1L);

        return user;
    }

    /**
     * Создание тестового токена
     */
    private AuthToken createRefreshToken(User user) {
        AuthToken authToken = new AuthToken(
                "valid-refresh-token",
                TokenType.REFRESH,
                Instant.now().plusSeconds(3600),
                user
        );
        authToken.setId(1L);

        return authToken;
    }
}

