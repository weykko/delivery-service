package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.mapper.UserMapper;
import naumen.project.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для операций с пользователями.
 * Предоставляет endpoints для получения, обновления и удаления данных текущего пользователя.
 * Требует аутентификации с JWT токеном.
 *
 * @see UserService
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Инициализация контроллера - внедрение зависимостей
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param user аутентифицированный пользователь
     * @return данные пользователя
     */
    @GetMapping("/me")
    public UserResponseDto getMyUser(@AuthenticationPrincipal User user) {
        return userMapper.toResponse(user);
    }

    /**
     * Обновляет информацию текущего пользователя.
     *
     * @param user аутентифицированный пользователь
     * @param request новые данные пользователя
     * @return обновленные данные пользователя
     */
    @PutMapping("/me")
    public UserResponseDto updateUser(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UpdateUserRequestDto request
    ) {
        User updatedUser = userMapper.updateUserEntityFromRequest(request, user);
        return userMapper.toResponse(userService.updateInfo(updatedUser));
    }

    /**
     * Удаляет текущего пользователя из системы.
     *
     * @param user аутентифицированный пользователь
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
    }
}
