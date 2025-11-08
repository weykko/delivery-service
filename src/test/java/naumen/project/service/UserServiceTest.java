package naumen.project.service;

import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.mapper.UserMapper;
import naumen.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для {@link UserService}
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private final User testUser = createTestUser();
    private UpdateUserRequestDto updateRequest = createUpdateUserRequest();
    private final UserResponseDto userResponse = createUserResponse();
    private final User updatedUser = createUpdatedUser();

    @Test
    void getInfoForUser_WithValidUser_ShouldReturnUserInfo() {
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        UserResponseDto result = userService.getInfoForUser(testUser);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void updateInfo_WithSamePhone_ShouldUpdateUser() {
        updateRequest = new UpdateUserRequestDto("Updated Name", testUser.getPhone());
        when(userMapper.updateUserEntityFromRequest(updateRequest, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(userResponse);

        UserResponseDto result = userService.updateInfo(testUser, updateRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository, never()).existsByPhone(any());
        verify(userMapper).updateUserEntityFromRequest(updateRequest, testUser);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toResponse(updatedUser);
    }

    @Test
    void updateInfo_WithNewUniquePhone_ShouldUpdateUser() {
        String newPhone = "+79997654321";
        updateRequest = new UpdateUserRequestDto("Updated Name", newPhone);

        when(userRepository.existsByPhone(newPhone)).thenReturn(false);
        when(userMapper.updateUserEntityFromRequest(updateRequest, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(userResponse);

        UserResponseDto result = userService.updateInfo(testUser, updateRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).existsByPhone(newPhone);
        verify(userMapper).updateUserEntityFromRequest(updateRequest, testUser);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toResponse(updatedUser);
    }

    @Test
    void updateInfo_WithExistingPhone_ShouldThrowException() {
        String existingPhone = "+79998887766";
        updateRequest = new UpdateUserRequestDto("Updated Name", existingPhone);

        when(userRepository.existsByPhone(existingPhone)).thenReturn(true);

        WebException exception = assertThrows(WebException.class,
                () -> userService.updateInfo(testUser, updateRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Телефон уже занят", exception.getMessage());
        verify(userRepository).existsByPhone(existingPhone);
        verify(userMapper, never()).updateUserEntityFromRequest(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_WithValidUser_ShouldDeleteUser() {
        userService.deleteUser(testUser);

        verify(userRepository).delete(testUser);
    }

    @Test
    void save_ShouldCallRepositorySave() {
        updateRequest = new UpdateUserRequestDto("Updated Name", testUser.getPhone());
        when(userMapper.updateUserEntityFromRequest(updateRequest, testUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(userResponse);

        userService.updateInfo(testUser, updateRequest);

        verify(userRepository).save(updatedUser);
    }

    // Вспомогательные методы для создания тестовых данных

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPhone("+79991234567");
        user.setRole(Role.USER);
        return user;
    }

    private UpdateUserRequestDto createUpdateUserRequest() {
        return new UpdateUserRequestDto("Updated Name", "+79991234567");
    }

    private UserResponseDto createUserResponse() {
        return new UserResponseDto(
                1L,
                "test@example.com",
                "Test User",
                "+79991234567",
                Role.USER
        );
    }

    private User createUpdatedUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Updated Name");
        user.setPhone("+79991234567");
        user.setRole(Role.USER);
        return user;
    }
}