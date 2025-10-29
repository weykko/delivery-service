package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Операции с пользователями
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Инициализация контроллера - внедрение зависимостей
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение информации о текущем авторизованном пользователе
     */
    @GetMapping("/me")
    public UserResponseDto getMyUser(@AuthenticationPrincipal User user) {
        return userService.getInfoForUser(user);
    }

    /**
     * Обновление текущего авторизованного пользователя
     */
    @PutMapping("/me")
    public UserResponseDto updateUser(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateUserRequestDto request
    ) {
        return userService.updateInfo(user, request);
    }

    /**
     * Удаление текущего авторизованного пользователя
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
    }
}
