package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.service.UserService;
import org.springframework.http.HttpStatus;
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

    /**
     * Инициализация контроллера - внедрение зависимостей
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param user аутентифицированный пользователь
     * @return данные пользователя
     */
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getInfoById(id);
    }

    /**
     * Обновляет информацию текущего пользователя.
     *
     * @param user аутентифицированный пользователь
     * @param request новые данные пользователя
     * @return обновленные данные пользователя
     */
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                                      @RequestBody @Valid UpdateUserRequestDto request) {
        return userService.updateInfoById(id, request);
    }

    /**
     * Удаляет текущего пользователя из системы.
     *
     * @param user аутентифицированный пользователь
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
