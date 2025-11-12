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

    private final User testUser = createTestUser();

    /**
     * Тестирование получения информации о текущем аутентифицированном пользователе
     */
    @Test
    void getMyUser_WithAuthenticatedUser_ShouldReturnUserResponse() {
        UserResponseDto expectedResponse = new UserResponseDto(
                testUser.getId(),
                testUser.getEmail(),
                testUser.getName(),
                testUser.getPhone(),
                testUser.getRole()
        );

        Mockito.when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        UserResponseDto result = userController.getMyUser(testUser);

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
    void updateUser_WithValidRequest_ShouldReturnUpdatedUser() {
        UpdateUserRequestDto updateRequest = new UpdateUserRequestDto(
                "Updated Name",
                "+79997654321",
                "Ул Пушкина");
        User updatedUser = new User();
        updatedUser.setId(testUser.getId());
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setName(updateRequest.name());
        updatedUser.setPhone(updateRequest.phone());
        updatedUser.setRole(testUser.getRole());

        UserResponseDto expectedResponse = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getPhone(),
                updatedUser.getRole()
        );

        Mockito.when(userMapper.updateUserEntityFromRequest(updateRequest, testUser)).thenReturn(updatedUser);
        Mockito.when(userService.updateInfo(updatedUser)).thenReturn(updatedUser);
        Mockito.when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userController.updateUser(testUser, updateRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResponse.name(), result.name());
        Assertions.assertEquals(expectedResponse.phone(), result.phone());
        Mockito.verify(userMapper).updateUserEntityFromRequest(updateRequest, testUser);
        Mockito.verify(userService).updateInfo(updatedUser);
        Mockito.verify(userMapper).toResponse(updatedUser);
    }

    /**
     * Тестирование успешного удаления текущего аутентифицированного пользователя
     */
    @Test
    void deleteUser_WithAuthenticatedUser_ShouldCallDeleteService() {
        userController.deleteUser(testUser);

        Mockito.verify(userService).deleteUser(testUser);
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPhone("+79991234567");
        user.setRole(Role.USER);
        return user;
    }
}

