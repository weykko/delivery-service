package naumen.project.controller;

import naumen.project.dto.paged.PagedResponseDto;
import naumen.project.dto.user.AdminUpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.InvalidInputException;
import naumen.project.mapper.PageMapper;
import naumen.project.mapper.UserMapper;
import naumen.project.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

/**
 * Модульные тесты для {@link AdminUserController}
 */
@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PageMapper pageMapper;

    @InjectMocks
    private AdminUserController adminUserController;

    private final User testUser = createTestUser();
    private final UserResponseDto testUserResponseDto = createTestUserResponse(testUser);
    private final AdminUpdateUserRequestDto testUserUpdateRequest = createTestUserUpdateRequest();

    /**
     * Тестирование успешного получения списка всех пользователей с пагинацией
     */
    @Test
    void getUsersShouldReturnPagedUsers() {
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<User> userPage = new PageImpl<>(List.of(testUser));
        PagedResponseDto<UserResponseDto> expectedResponse = new PagedResponseDto<>(
                List.of(testUserResponseDto), 0, 10, 1, 1
        );

        Mockito.when(userService.getAllUsers(pageRequest)).thenReturn(userPage);
        Mockito.when(userMapper.toResponse(testUser)).thenReturn(testUserResponseDto);
        Mockito.when(pageMapper.toUserResponse(Mockito.any())).thenReturn(expectedResponse);

        PagedResponseDto<UserResponseDto> result = adminUserController.getUsers(page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse, result);
        Mockito.verify(userService).getAllUsers(pageRequest);
        Mockito.verify(userMapper).toResponse(testUser);
        Mockito.verify(pageMapper).toUserResponse(Mockito.any());
    }

    /**
     * Тестирование успешного получения пользователя по идентификатору
     */
    @Test
    void getUserShouldReturnUserDetails() {
        Long userId = 1L;

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(userMapper.toResponse(testUser)).thenReturn(testUserResponseDto);

        UserResponseDto result = adminUserController.getUser(userId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testUserResponseDto, result);
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userMapper).toResponse(testUser);
    }

    /**
     * Тестирование получения несуществующего пользователя
     */
    @Test
    void getUserNotFoundShouldThrowException() {
        Long userId = 999L;
        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> adminUserController.getUser(userId));
        Assertions.assertEquals("Пользователь с id '999' не найден", exception.getMessage());
        Mockito.verify(userService).getUserById(userId);
    }

    /**
     * Тестирование успешного обновления пользователя
     */
    @Test
    void updateUserShouldUpdateAndReturnUser() {
        Long userId = 1L;
        UserResponseDto expectedResponse = new UserResponseDto(
                testUser.getId(),
                testUserUpdateRequest.email(),
                testUserUpdateRequest.role(),
                testUserUpdateRequest.name(),
                testUserUpdateRequest.phone(),
                testUserUpdateRequest.address()
        );

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(userService.updateInfo(testUser, testUserUpdateRequest.phone(), testUserUpdateRequest.email()))
                .thenReturn(testUser);
        Mockito.when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        UserResponseDto result = adminUserController.updateUser(userId, testUserUpdateRequest);

        Assertions.assertEquals(expectedResponse, result);
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userService).updateInfo(testUser, testUserUpdateRequest.phone(), testUserUpdateRequest.email());
        Mockito.verify(userMapper).toResponse(testUser);

        Assertions.assertEquals(testUserUpdateRequest.name(), testUser.getName());
        Assertions.assertEquals(testUserUpdateRequest.role(), testUser.getRole());
        Assertions.assertEquals(testUserUpdateRequest.address(), testUser.getAddress());
    }

    /**
     * Тестирование обновления несуществующего пользователя
     */
    @Test
    void updateUserWithInvalidIdShouldThrowException() {
        Long userId = 999L;

        Mockito.when(userService.getUserById(userId)).thenReturn(Optional.empty());

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> adminUserController.updateUser(userId, testUserUpdateRequest));

        Assertions.assertEquals("Не удалось обновить пользователя, причина: Пользователь с id '999' не найден",
                exception.getMessage());
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userService, Mockito.never()).updateInfo(Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создание тестового пользователя
     */
    private User createTestUser() {
        User user = new User(
                "test@example.com",
                "Test User",
                "+79991234567",
                Role.CLIENT,
                "Пушкина 17"
        );
        user.setId(1L);

        return user;
    }

    /**
     * Создание тестового DTO ответа пользователя
     */
    private UserResponseDto createTestUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getName(),
                user.getPhone(),
                user.getAddress()
        );
    }

    /**
     * Создание тестового DTO запроса обновления пользователя
     */
    private AdminUpdateUserRequestDto createTestUserUpdateRequest() {
        return new AdminUpdateUserRequestDto(
                "new@example.com",
                Role.ADMIN,
                "New Name",
                "New Phone",
                "New Address"
        );
    }
}

