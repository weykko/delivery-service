package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.auth.*;
import naumen.project.entity.User;
import naumen.project.service.AuthService;
import naumen.project.service.AuthTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер с операции аутентификации, работы с токенами
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;

    /**
     * Инициализация контролера
     */
    public AuthController(
            AuthService authService,
            AuthTokenService authTokenService
    ) {
        this.authService = authService;
        this.authTokenService = authTokenService;
    }

    /**
     * Регистрация пользователя
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDto register(@RequestBody @Valid RegisterRequestDto request) {
        return authService.register(request);
    }

    /**
     * Аутентификация пользователя
     */
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return authService.login(request);
    }

    /**
     * Обновление jwt токенов
     */
    @PostMapping("/refresh")
    public TokenResponseDto refresh(@RequestBody @Valid RefreshRequestDto request) {
        return authTokenService.refresh(request);
    }

    /**
     * Отзыв всех токенов
     */
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal User user) {
        authTokenService.logout(user);
    }

}
