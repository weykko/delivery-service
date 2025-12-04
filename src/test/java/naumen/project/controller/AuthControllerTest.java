package naumen.project.controller;

import naumen.project.dto.auth.*;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.UserMapper;
import naumen.project.service.AuthService;
import naumen.project.service.AuthTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Модульные тесты для {@link AuthController}
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    /**
     * Тестирование успешной регистрации нового пользователя
     */
    @Test
    void register_WithValidRequest_ShouldReturnRegisterResponse() {
        RegisterRequestDto registerRequest = createRegisterRequest();
        User testUser = createTestUser(1L);

        RegisterResponseDto expectedResponse = new RegisterResponseDto(
                1L,
                registerRequest.email(),
                registerRequest.name(),
                registerRequest.role()
        );

        Mockito.when(authService.register(Mockito.any(User.class), Mockito.eq(registerRequest.password())))
                .thenReturn(testUser);
        Mockito.when(userMapper.toRegisterResponse(testUser)).thenReturn(expectedResponse);

        RegisterResponseDto result = authController.register(registerRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.email(), result.email());
        Assertions.assertEquals(expectedResponse.name(), result.name());
        Assertions.assertEquals(expectedResponse.role(), result.role());
        Mockito.verify(authService).register(Mockito.any(User.class), Mockito.eq(registerRequest.password()));
        Mockito.verify(userMapper).toRegisterResponse(testUser);
    }

    /**
     * Тестирование успешного входа пользователя с корректными учетными данными
     */
    @Test
    void login_WithValidCredentials_ShouldReturnTokenResponse() {
        LoginRequestDto loginRequest = createLoginRequest();
        TokenResponseDto expectedTokens = new TokenResponseDto("accessToken", "refreshToken");

        Mockito.when(authService.login(loginRequest.email(), loginRequest.password()))
                .thenReturn(expectedTokens);

        TokenResponseDto result = authController.login(loginRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedTokens.accessToken(), result.accessToken());
        Assertions.assertEquals(expectedTokens.refreshToken(), result.refreshToken());
        Mockito.verify(authService).login(loginRequest.email(), loginRequest.password());
    }

    /**
     * Тестирование обновления токенов с валидным refresh токеном
     */
    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewTokens() {
        RefreshRequestDto refreshRequest = new RefreshRequestDto("validRefreshToken");
        TokenResponseDto expectedTokens = new TokenResponseDto("newAccessToken", "newRefreshToken");

        Mockito.when(authTokenService.refresh(refreshRequest.refreshToken())).thenReturn(expectedTokens);

        TokenResponseDto result = authController.refresh(refreshRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedTokens.accessToken(), result.accessToken());
        Assertions.assertEquals(expectedTokens.refreshToken(), result.refreshToken());
        Mockito.verify(authTokenService).refresh(refreshRequest.refreshToken());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser(Long id) {
        User user = new User("test@example.com", "Test User", "+79991234567", Role.USER);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }

    /**
     * Создает тестовый запрос регистрации
     */
    private RegisterRequestDto createRegisterRequest() {
        return new RegisterRequestDto(
                "test@example.com",
                "password123",
                Role.USER,
                "Test User",
                "+79991234567"
        );
    }

    /**
     * Создает тестовый запрос входа
     */
    private LoginRequestDto createLoginRequest() {
        return new LoginRequestDto(
                "test@example.com",
                "password123"
        );
    }
}

