package naumen.project.service;

import naumen.project.auth.AuthProps;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.entity.enums.TokenType;
import naumen.project.exception.IllegalDataException;
import naumen.project.exception.NotFoundException;
import naumen.project.repository.AuthTokenRepository;
import naumen.project.util.JwtUtil;
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
    private final String accessToken = "access-token-123";
    private final String refreshToken = "refresh-token-456";
    private final AuthToken authRefreshToken = createAuthToken(refreshToken, TokenType.REFRESH);

    /**
     * Тестирование проверки допустимости токена
     */
    @Test
    void isTokenAllowed_WithValidToken_ShouldReturnTrue() {
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
    void isTokenAllowed_WithInvalidToken_ShouldReturnFalse() {
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
    void generateAndSave_WithValidUser_ShouldGenerateAndSaveTokens() {
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
    void refresh_WithValidRefreshToken_ShouldReturnNewTokens() {
        TokenResponseDto newTokens = new TokenResponseDto("new-access", "new-refresh");

        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        Mockito.when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.of(authRefreshToken));

        AuthTokenService spyService = Mockito.spy(authTokenService);
        Mockito.doReturn(newTokens).when(spyService).generateAndSave(testUser);

        TokenResponseDto result = spyService.refresh(refreshToken);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(newTokens, result);
        Mockito.verify(jwtUtil).validateRefreshToken(refreshToken);
        Mockito.verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        Mockito.verify(authTokenRepository).findByTokenAndType(refreshToken, TokenType.REFRESH);
        Mockito.verify(spyService).generateAndSave(testUser);
    }

    /**
     * Тестирование обновления токенов с невалидным рефреш токеном
     */
    @Test
    void refresh_WithInvalidJwtToken_ShouldThrowException() {
        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен невалиден", exception.getMessage());
        Mockito.verify(jwtUtil).validateRefreshToken(refreshToken);
        Mockito.verify(authTokenRepository, Mockito.never()).existsByTokenAndType(Mockito.any(), Mockito.any());
    }

    /**
     * Тестирование обновления токенов с отсутствующим в базе рефреш токеном
     */
    @Test
    void refresh_WithTokenNotInDatabase_ShouldThrowException() {
        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(false);

        IllegalDataException exception = Assertions.assertThrows(IllegalDataException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен невалиден", exception.getMessage());
        Mockito.verify(jwtUtil).validateRefreshToken(refreshToken);
        Mockito.verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        Mockito.verify(authTokenRepository, Mockito.never()).findByTokenAndType(Mockito.any(), Mockito.any());
    }

    /**
     * Тестирование выхода пользователя
     */
    @Test
    void logout_WithValidUser_ShouldRemoveAllUserTokens() {
        authTokenService.logout(testUser);

        Mockito.verify(authTokenRepository).removeAllByUser(testUser);
    }

    /**
     * Тестирование генерации access токена
     */
    @Test
    void generateAccessToken_ShouldCreateAndSaveToken() {
        Mockito.when(authProps.getAccess()).thenReturn(accessTokenConfig);
        Mockito.when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        Mockito.when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);
        Mockito.when(authTokenRepository.save(Mockito.any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        Mockito.when(refreshTokenConfig.getLifetime()).thenReturn(86400L);
        Mockito.when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);

        authTokenService.generateAndSave(testUser);

        Mockito.verify(jwtUtil).generateAccessToken(testUser);
        Mockito.verify(authTokenRepository, Mockito.atLeastOnce()).save(Mockito.argThat(token ->
                token.getType() == TokenType.ACCESS &&
                token.getToken().equals(accessToken) &&
                token.getUser().equals(testUser)
        ));
    }

    /**
     * Тестирование генерации refresh токена
     */
    @Test
    void generateRefreshToken_ShouldCreateAndSaveToken() {
        Mockito.when(authProps.getAccess()).thenReturn(accessTokenConfig);
        Mockito.when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        Mockito.when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);

        Mockito.when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        Mockito.when(refreshTokenConfig.getLifetime()).thenReturn(86400L);
        Mockito.when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);
        Mockito.when(authTokenRepository.save(Mockito.any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authTokenService.generateAndSave(testUser);

        Mockito.verify(jwtUtil).generateRefreshToken(testUser);
        Mockito.verify(authTokenRepository, Mockito.atLeastOnce()).save(Mockito.argThat(token ->
                token.getType() == TokenType.REFRESH &&
                token.getToken().equals(refreshToken) &&
                token.getUser().equals(testUser)
        ));
    }

    /**
     * Тестирование получения токена по значению
     */
    @Test
    void getToken_WithExistingToken_ShouldReturnToken() {
        Mockito.when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.of(authRefreshToken));

        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);

        AuthTokenService spyService = Mockito.spy(authTokenService);
        Mockito.doReturn(new TokenResponseDto("new-access", "new-refresh"))
                .when(spyService).generateAndSave(testUser);

        spyService.refresh(refreshToken);

        Mockito.verify(authTokenRepository).findByTokenAndType(refreshToken, TokenType.REFRESH);
    }

    /**
     * Тестирование получения токена по значению, когда токен не найден
     */
    @Test
    void getToken_WithNonExistingToken_ShouldThrowException() {
        Mockito.when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        Mockito.when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        Mockito.when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> authTokenService.refresh(refreshToken));

        Assertions.assertEquals("Токен не найден", exception.getMessage());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(Role.CLIENT);

        return user;
    }

    /**
     * Создание тестового токена
     */
    private AuthToken createAuthToken(String token, TokenType type) {

        AuthToken authToken = new AuthToken();
        authToken.setId(1L);
        authToken.setToken(token);
        authToken.setType(type);
        authToken.setExpireAt(Instant.now().plusSeconds(3600));
        authToken.setUser(testUser);
        return authToken;
    }
}