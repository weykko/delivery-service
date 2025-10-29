package naumen.project.service;

import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.mapper.UserMapper;
import naumen.project.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестируем операции с пользователями
 */
public class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);

    private final UserService userService = new UserService(userRepository, userMapper);

    /**
     * Тестируем, что возвращаем дто с информацией о пользователе
     */
    @Test
    public void testInfoForUser() {
        User user = new User();
        UserResponseDto dto = new UserResponseDto(
                1L,
                "test@notexists@ru",
                "Alexey",
                "73454562345",
                Role.ROLE_USER.name()
        );

        when(userMapper.toInfoDto(user)).thenReturn(dto);

        UserResponseDto result = userService.getInfoForUser(user);

        assertEquals(dto, result);
        verify(userMapper).toInfoDto(user);
    }

    /**
     * Тестируем обновления пользователя
     */
    @Test
    void testUpdateInfo() {
        User user = new User();
        UpdateUserRequestDto request = new UpdateUserRequestDto("NameNew", "PhoneNew");

        User updatedUser = new User();
        UserResponseDto dto = new UserResponseDto(
                1L,
                "test@notexists@ru",
                "NameNew",
                "PhoneNew",
                Role.ROLE_USER.name()
        );

        when(userMapper.updateUser(user, request)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toInfoDto(updatedUser)).thenReturn(dto);

        UserResponseDto result = userService.updateInfo(user, request);

        assertEquals(dto, result);
        verify(userMapper).updateUser(user, request);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toInfoDto(updatedUser);
    }

    /**
     * Тестируем удаление пользователя
     */
    @Test
    void testDeleteUser() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

}
