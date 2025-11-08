package naumen.project.service;

import naumen.project.dto.auth.RefreshRequestDto;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.AuthToken;
import naumen.project.entity.User;
import naumen.project.entity.enums.TokenType;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.auth.AuthProps;
import naumen.project.repository.AuthTokenRepository;
import naumen.project.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private final AuthToken authAccessToken = createAuthToken(accessToken, TokenType.ACCESS);
    private final AuthToken authRefreshToken = createAuthToken(refreshToken, TokenType.REFRESH);

    @Test
    void isTokenAllowed_WithValidToken_ShouldReturnTrue() {
        when(authTokenRepository.existsByTokenAndType(accessToken, TokenType.ACCESS))
                .thenReturn(true);

        boolean result = authTokenService.isTokenAllowed(accessToken, TokenType.ACCESS);

        assertTrue(result);
        verify(authTokenRepository).existsByTokenAndType(accessToken, TokenType.ACCESS);
    }

    @Test
    void isTokenAllowed_WithInvalidToken_ShouldReturnFalse() {
        when(authTokenRepository.existsByTokenAndType(accessToken, TokenType.ACCESS))
                .thenReturn(false);

        boolean result = authTokenService.isTokenAllowed(accessToken, TokenType.ACCESS);

        assertFalse(result);
        verify(authTokenRepository).existsByTokenAndType(accessToken, TokenType.ACCESS);
    }

    @Test
    void generateAndSave_WithValidUser_ShouldGenerateAndSaveTokens() {
        when(authProps.getAccess()).thenReturn(accessTokenConfig);
        when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        when(refreshTokenConfig.getLifetime()).thenReturn(86400L);

        when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponseDto result = authTokenService.generateAndSave(testUser);

        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(jwtUtil).generateAccessToken(testUser);
        verify(jwtUtil).generateRefreshToken(testUser);
        verify(authTokenRepository, times(2)).save(any(AuthToken.class));
        verify(authProps).getAccess();
        verify(authProps).getRefresh();
    }

    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewTokens() {
        RefreshRequestDto request = new RefreshRequestDto(refreshToken);
        TokenResponseDto newTokens = new TokenResponseDto("new-access", "new-refresh");

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.of(authRefreshToken));

        AuthTokenService spyService = spy(authTokenService);
        doReturn(newTokens).when(spyService).generateAndSave(testUser);

        TokenResponseDto result = spyService.refresh(request);

        assertNotNull(result);
        assertEquals(newTokens, result);
        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        verify(authTokenRepository).findByTokenAndType(refreshToken, TokenType.REFRESH);
        verify(spyService).generateAndSave(testUser);
    }

    @Test
    void refresh_WithInvalidJwtToken_ShouldThrowException() {

        RefreshRequestDto request = new RefreshRequestDto(refreshToken);
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        WebException exception = assertThrows(WebException.class,
                () -> authTokenService.refresh(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Токен невалиден", exception.getMessage());
        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(authTokenRepository, never()).existsByTokenAndType(any(), any());
    }

    @Test
    void refresh_WithTokenNotInDatabase_ShouldThrowException() {
        RefreshRequestDto request = new RefreshRequestDto(refreshToken);
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(false);

        WebException exception = assertThrows(WebException.class,
                () -> authTokenService.refresh(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Токен невалиден", exception.getMessage());
        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        verify(authTokenRepository, never()).findByTokenAndType(any(), any());
    }

    @Test
    void refresh_WithTokenNotFoundInDatabase_ShouldThrowException() {
        RefreshRequestDto request = new RefreshRequestDto(refreshToken);
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.empty());

        WebException exception = assertThrows(WebException.class,
                () -> authTokenService.refresh(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Токен не найден", exception.getMessage());
        verify(jwtUtil).validateRefreshToken(refreshToken);
        verify(authTokenRepository).existsByTokenAndType(refreshToken, TokenType.REFRESH);
        verify(authTokenRepository).findByTokenAndType(refreshToken, TokenType.REFRESH);
    }

    @Test
    void logout_WithValidUser_ShouldRemoveAllUserTokens() {
        authTokenService.logout(testUser);

        verify(authTokenRepository).removeAllByUser(testUser);
    }

    @Test
    void generateAccessToken_ShouldCreateAndSaveToken() {
        when(authProps.getAccess()).thenReturn(accessTokenConfig);
        when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        when(refreshTokenConfig.getLifetime()).thenReturn(86400L);
        when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);

        TokenResponseDto result = authTokenService.generateAndSave(testUser);

        verify(jwtUtil).generateAccessToken(testUser);
        verify(authTokenRepository, atLeastOnce()).save(argThat(token ->
                token.getType() == TokenType.ACCESS &&
                        token.getToken().equals(accessToken) &&
                        token.getUser().equals(testUser)
        ));
    }

    @Test
    void generateRefreshToken_ShouldCreateAndSaveToken() {
        when(authProps.getAccess()).thenReturn(accessTokenConfig);
        when(accessTokenConfig.getLifetime()).thenReturn(3600L);
        when(jwtUtil.generateAccessToken(testUser)).thenReturn(accessToken);

        when(authProps.getRefresh()).thenReturn(refreshTokenConfig);
        when(refreshTokenConfig.getLifetime()).thenReturn(86400L);
        when(jwtUtil.generateRefreshToken(testUser)).thenReturn(refreshToken);
        when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponseDto result = authTokenService.generateAndSave(testUser);

        verify(jwtUtil).generateRefreshToken(testUser);
        verify(authTokenRepository, atLeastOnce()).save(argThat(token ->
                token.getType() == TokenType.REFRESH &&
                        token.getToken().equals(refreshToken) &&
                        token.getUser().equals(testUser)
        ));
    }

    @Test
    void getToken_WithExistingToken_ShouldReturnToken() {
        when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.of(authRefreshToken));

        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);

        RefreshRequestDto request = new RefreshRequestDto(refreshToken);
        AuthTokenService spyService = spy(authTokenService);
        doReturn(new TokenResponseDto("new-access", "new-refresh"))
                .when(spyService).generateAndSave(testUser);

        spyService.refresh(request);

        verify(authTokenRepository).findByTokenAndType(refreshToken, TokenType.REFRESH);
    }

    @Test
    void getToken_WithNonExistingToken_ShouldThrowException() {
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(authTokenRepository.existsByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(true);
        when(authTokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH))
                .thenReturn(Optional.empty());

        RefreshRequestDto request = new RefreshRequestDto(refreshToken);

        WebException exception = assertThrows(WebException.class,
                () -> authTokenService.refresh(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Токен не найден", exception.getMessage());
    }

    // Вспомогательные методы для создания тестовых данных

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(Role.USER);
        return user;
    }

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
