package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.auth.*;
import naumen.project.entity.User;
import naumen.project.mapper.UserMapper;
import naumen.project.service.AuthService;
import naumen.project.service.AuthTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для операций аутентификации и управления токенами.
 * Обеспечивает регистрацию, вход, обновление токенов и выход из системы.
 *
 * @see AuthService
 * @see AuthTokenService
 * @see UserMapper
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;
    private final UserMapper userMapper;

    /**
     * Инициализация контролера
     */
    public AuthController(
            AuthService authService,
            AuthTokenService authTokenService, UserMapper userMapper
    ) {
        this.authService = authService;
        this.authTokenService = authTokenService;
        this.userMapper = userMapper;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param request данные для регистрации пользователя
     * @return информация о зарегистрированном пользователе
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public RegisterResponseDto register(@RequestBody @Valid RegisterRequestDto request) {
        authService.checkUniqueFieldsRegistration(request);
        User user = userMapper.toEntity(request);

        return userMapper.toRegisterResponse(authService.register(user, request.password()));
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param request учетные данные пользователя
     * @return пара access и refresh токенов
     */
    @PostMapping("/login")
    @Transactional
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        String email = request.email();
        String password = request.password();
        return authService.login(email, password);
    }

    /**
     * Обновляет JWT токены по валидному refresh токену.
     *
     * @param request запрос с refresh токеном
     * @return новая пара access и refresh токенов
     */
    @PostMapping("/refresh")
    public TokenResponseDto refresh(@RequestBody @Valid RefreshRequestDto request) {
        return authTokenService.refresh(request);
    }

    /**
     * Выполняет выход пользователя из системы и отзывает все токены.
     *
     * @param user аутентифицированный пользователь
     */
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal User user) {
        authTokenService.logout(user);
    }

}
