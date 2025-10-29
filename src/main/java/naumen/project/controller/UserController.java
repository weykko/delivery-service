package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.service.UserService;
import org.springframework.http.HttpStatus;
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
     * Получение информации о пользователе по id
     */
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getInfoById(id);
    }

    /**
     * Обновление пользователя по id
     */
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                                      @RequestBody @Valid UpdateUserRequestDto request) {
        return userService.updateInfoById(id, request);
    }

    /**
     * Удаление пользователя по id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
