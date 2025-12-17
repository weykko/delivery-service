package naumen.project.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.dto.user.AdminUpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.mapper.PageMapper;
import naumen.project.mapper.UserMapper;
import naumen.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для управления пользователями администраторами.
 * Предоставляет endpoints для операций с пользователями.
 * Требует аутентификации с JWT токеном.
 *
 * @see UserService
 * @see UserMapper
 * @see PageMapper
 */
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    public AdminUserController(UserService userService, UserMapper userMapper, PageMapper pageMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.pageMapper = pageMapper;
    }

    /**
     * Получает всех пользователей с пагинацией.
     *
     * @param page номер страницы
     * @param size количество элементов на странице
     * @return страница с пользователями
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public PagedResponseDto<UserResponseDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserResponseDto> userPages = userService
                .getAllUsers(PageRequest.of(page, size))
                .map(userMapper::toResponse);

        return pageMapper.toUserResponse(userPages);
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return данные пользователя
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public UserResponseDto getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return userMapper.toResponse(user);
    }

    /**
     * Обновляет информацию о пользователе по идентификатору.
     *
     * @param id      идентификатор пользователя
     * @param request данные для обновления пользователя
     * @return обновленные данные пользователя
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public UserResponseDto updateUser(@PathVariable Long id,
                                      @RequestBody @Valid AdminUpdateUserRequestDto request) {
        User user = userService.getUserById(id);

        Optional.ofNullable(request.name()).ifPresent(user::setName);
        Optional.ofNullable(request.role()).ifPresent(user::setRole);
        Optional.ofNullable(request.address()).ifPresent(user::setAddress);

        String phone = request.phone();
        String email = request.email();

        userService.updateInfo(user, phone, email);

        return userMapper.toResponse(user);
    }
}
