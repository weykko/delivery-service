package naumen.project.service;

import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * Модульные тесты для {@link UserService}
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final User testUser = createTestUser();

    /**
     * Тестирование успешного обновления информации пользователя с тем же номером телефона
     */
    @Test
    void updateInfo_WithSamePhone_ShouldUpdateUser() {
        User userToUpdate = new User();
        userToUpdate.setId(testUser.getId());
        userToUpdate.setEmail(testUser.getEmail());
        userToUpdate.setName("Updated Name");
        userToUpdate.setPhone(testUser.getPhone());
        userToUpdate.setRole(testUser.getRole());

        Mockito.when(userRepository.findByPhone(testUser.getPhone())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User result = userService.updateInfo(userToUpdate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userToUpdate.getName(), result.getName());
        Mockito.verify(userRepository).findByPhone(testUser.getPhone());
        Mockito.verify(userRepository).save(userToUpdate);
    }

    /**
     * Тестирование успешного обновления информации пользователя с новым уникальным номером телефона
     */
    @Test
    void updateInfo_WithNewUniquePhone_ShouldUpdateUser() {
        String newPhone = "+79997654321";
        User userToUpdate = new User();
        userToUpdate.setId(testUser.getId());
        userToUpdate.setEmail(testUser.getEmail());
        userToUpdate.setName("Updated Name");
        userToUpdate.setPhone(newPhone);
        userToUpdate.setRole(testUser.getRole());

        Mockito.when(userRepository.findByPhone(newPhone)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User result = userService.updateInfo(userToUpdate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(newPhone, result.getPhone());
        Mockito.verify(userRepository).findByPhone(newPhone);
        Mockito.verify(userRepository).save(userToUpdate);
    }

    /**
     * Тестирование обновления информации пользователя с уже существующим номером телефона
     */
    @Test
    void updateInfo_WithExistingPhone_ShouldThrowException() {
        String existingPhone = "+79998887766";
        User userToUpdate = new User();
        userToUpdate.setId(2L); // другой ID
        userToUpdate.setEmail("another@example.com");
        userToUpdate.setName("Updated Name");
        userToUpdate.setPhone(existingPhone);
        userToUpdate.setRole(Role.CLIENT);

        User existingUser = new User();
        existingUser.setId(3L); // другой пользователь с таким телефоном
        existingUser.setPhone(existingPhone);

        Mockito.when(userRepository.findByPhone(existingPhone)).thenReturn(Optional.of(existingUser));

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> userService.updateInfo(userToUpdate));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Телефон уже занят", exception.getMessage());
        Mockito.verify(userRepository).findByPhone(existingPhone);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование успешного удаления пользователя
     */
    @Test
    void deleteUser_WithValidUser_ShouldDeleteUser() {
        userService.deleteUser(testUser);

        Mockito.verify(userRepository).delete(testUser);
    }

    /**
     * Тестирование вызова метода сохранения пользователя
     */
    @Test
    void saveUser_ShouldCallRepositorySave() {
        User userToUpdate = new User();
        userToUpdate.setId(testUser.getId());
        userToUpdate.setEmail(testUser.getEmail());
        userToUpdate.setName("Updated Name");
        userToUpdate.setPhone(testUser.getPhone());
        userToUpdate.setRole(testUser.getRole());

        Mockito.when(userRepository.findByPhone(testUser.getPhone())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        userService.updateInfo(userToUpdate);

        Mockito.verify(userRepository).save(userToUpdate);
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
        user.setRole(Role.CLIENT);
        return user;
    }
}






