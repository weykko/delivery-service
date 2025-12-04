package naumen.project.service;

import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
import naumen.project.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    /**
     * Тестирование успешного обновления информации пользователя с тем же номером телефона
     */
    @Test
    void updateInfo_WithSamePhone_ShouldUpdateUser() {
        User existingUser = createTestUser(1L, "test@example.com", "Test User", "+79991234567");
        User userToUpdate = createTestUser(1L, "test@example.com", "Updated Name", "+79991234567");

        Mockito.when(userRepository.findByPhone("+79991234567")).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User result = userService.updateInfo(userToUpdate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Name", result.getName());
        Mockito.verify(userRepository).findByPhone("+79991234567");
        Mockito.verify(userRepository).save(userToUpdate);
    }

    /**
     * Тестирование успешного обновления информации пользователя с новым уникальным номером телефона
     */
    @Test
    void updateInfo_WithNewUniquePhone_ShouldUpdateUser() {
        String newPhone = "+79997654321";
        User userToUpdate = createTestUser(1L, "test@example.com", "Updated Name", newPhone);

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
        User userToUpdate = createTestUser(2L, "another@example.com", "Updated Name", existingPhone);
        User existingUser = createTestUser(3L, "existing@example.com", "Existing User", existingPhone);

        Mockito.when(userRepository.findByPhone(existingPhone)).thenReturn(Optional.of(existingUser));

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> userService.updateInfo(userToUpdate));

        Assertions.assertEquals("Телефон уже занят", exception.getMessage());
        Mockito.verify(userRepository).findByPhone(existingPhone);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирование проверки уникальных полей при регистрации - email занят
     */
    @Test
    void checkUniqueFieldsRegistration_WithExistingEmail_ShouldThrowException() {
        User newUser = createTestUser(null, "test@example.com", "New User", "+79991111111");

        Mockito.when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> userService.checkUniqueFieldsRegistration(newUser));

        Assertions.assertEquals("Email уже занят", exception.getMessage());
        Mockito.verify(userRepository).existsByEmail("test@example.com");
    }

    /**
     * Тестирование проверки уникальных полей при регистрации - телефон занят
     */
    @Test
    void checkUniqueFieldsRegistration_WithExistingPhone_ShouldThrowException() {
        User newUser = createTestUser(null, "newemail@example.com", "New User", "+79991234567");

        Mockito.when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        Mockito.when(userRepository.existsByPhone("+79991234567")).thenReturn(true);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> userService.checkUniqueFieldsRegistration(newUser));

        Assertions.assertEquals("Телефон уже занят", exception.getMessage());
        Mockito.verify(userRepository).existsByEmail("newemail@example.com");
        Mockito.verify(userRepository).existsByPhone("+79991234567");
    }

    /**
     * Тестирование успешной проверки уникальных полей при регистрации
     */
    @Test
    void checkUniqueFieldsRegistration_WithUniqueFields_ShouldNotThrowException() {
        User newUser = createTestUser(null, "newemail@example.com", "New User", "+79991111111");

        Mockito.when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        Mockito.when(userRepository.existsByPhone("+79991111111")).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> userService.checkUniqueFieldsRegistration(newUser));

        Mockito.verify(userRepository).existsByEmail("newemail@example.com");
        Mockito.verify(userRepository).existsByPhone("+79991111111");
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser(Long id, String email, String name, String phone) {
        User user = new User(email, name, phone, Role.USER);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }
}






