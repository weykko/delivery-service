package naumen.project.controller;

import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.UserMapper;
import naumen.project.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Модульные тесты для {@link UserController}
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    /**
     * Тестирование получения информации о текущем аутентифицированном пользователе
     */
    @Test
    void getUserWithAuthenticatedUserShouldReturnUserResponse() {
        User testUser = createTestUser(1L);
        UserResponseDto expectedResponse = new UserResponseDto(
                testUser.getId(),
                testUser.getEmail(),
                testUser.getRole(),
                testUser.getName(),
                testUser.getPhone(),
                "Пушкина 17"
        );

        Mockito.when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        UserResponseDto result = userController.getUser(testUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.id(), result.id());
        Assertions.assertEquals(expectedResponse.email(), result.email());
        Assertions.assertEquals(expectedResponse.name(), result.name());
        Assertions.assertEquals(expectedResponse.phone(), result.phone());
        Assertions.assertEquals(expectedResponse.role(), result.role());
        Mockito.verify(userMapper).toResponse(testUser);
    }

    /**
     * Тестирование успешного обновления информации пользователя с валидными данными
     */
    @Test
    void updateUserWithValidRequestShouldReturnUpdatedUser() {
        User testUser = createTestUser(2L);
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto("Updated Name", "+79997654321",
                "Пушкина 17");

        UserResponseDto expectedResponse = new UserResponseDto(
                testUser.getId(),
                testUser.getEmail(),
                testUser.getRole(),
                updateRequest.name(),
                updateRequest.phone(),
                "Пушкина 17"
        );

        Mockito.when(userService.updateInfo(testUser, updateRequest.phone(), null)).thenReturn(testUser);
        Mockito.when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        UserResponseDto result = userController.updateUser(testUser, updateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.name(), result.name());
        Assertions.assertEquals(expectedResponse.phone(), result.phone());
        Mockito.verify(userService).updateInfo(testUser, updateRequest.phone(), null);
        Mockito.verify(userMapper).toResponse(testUser);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser(Long id) {
        User user = new User("test@example.com", "Test User", "+79991234567", Role.CLIENT);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }
}

