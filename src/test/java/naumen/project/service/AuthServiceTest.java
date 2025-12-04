package naumen.project.service;

import naumen.project.auth.JwtUserDetails;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Модульные тесты для {@link AuthService}
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    /**
     * Тестирование успешной регистрации пользователя с валидными данными
     */
    @Test
    public void registerWithValidDataShouldReturnSuccessResponse() {
        User testUser = createTestUser(null);
        String password = "strongPassword";
        String encodedPassword = "encodedPassword";

        Mockito.doNothing().when(userService).checkUniqueFieldsRegistration(testUser);
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Mockito.doNothing().when(userService).saveUser(testUser);

        User result = authService.register(testUser, password);

        Assertions.assertEquals(testUser.getEmail(), result.getEmail());
        Assertions.assertEquals(testUser.getRole(), result.getRole());
        Assertions.assertEquals(testUser.getName(), result.getName());
        Assertions.assertEquals(encodedPassword, result.getPassword());
        Mockito.verify(userService).checkUniqueFieldsRegistration(testUser);
        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(userService).saveUser(testUser);
    }

    /**
     * Тестирование регистрации пользователя с уже существующим email
     */
    @Test
    public void registerWithExistingEmailShouldThrowWebException() {
        User testUser = createTestUser(null);
        String password = "strongPassword";

        Mockito.doThrow(new InvalidInputException("Email уже занят"))
                .when(userService).checkUniqueFieldsRegistration(testUser);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> authService.register(testUser, password));
        Assertions.assertEquals("Email уже занят", exception.getMessage());
    }

    /**
     * Тестирование регистрации пользователя с уже существующим номером телефона
     */
    @Test
    public void registerWithExistingPhoneShouldThrowWebException() {
        User testUser = createTestUser(null);
        String password = "strongPassword";

        Mockito.doThrow(new InvalidInputException("Телефон уже занят"))
                .when(userService).checkUniqueFieldsRegistration(testUser);

        InvalidInputException exception = Assertions.assertThrows(InvalidInputException.class,
                () -> authService.register(testUser, password));
        Assertions.assertEquals("Телефон уже занят", exception.getMessage());
        Mockito.verify(userService).checkUniqueFieldsRegistration(testUser);
    }

    /**
     * Тестирование успешного входа пользователя с валидными учетными данными
     */
    @Test
    public void loginWithValidCredentialsShouldReturnTokens() {
        User testUser = createTestUser(1L);
        String password = "strongPassword";
        String aToken = "access-token";
        String rToken = "refresh-token";

        Mockito.when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), password))
        ).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserDetails(testUser, null),
                        null
                )
        );
        Mockito.when(authTokenService.generateAndSave(testUser)).thenReturn(new TokenResponseDto(aToken, rToken));

        TokenResponseDto result = authService.login(testUser.getEmail(), password);

        Assertions.assertAll(
                () -> Assertions.assertEquals(aToken, result.accessToken()),
                () -> Assertions.assertEquals(rToken, result.refreshToken())
        );
    }

    /**
     * Тестирование входа пользователя с невалидными учетными данными
     */
    @Test
    public void loginWithInvalidCredentialsShouldThrowBadCredentialsException() {
        User testUser = createTestUser(1L);
        String password = "wrongPassword";

        Mockito.when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(testUser.getEmail(), password))
        ).thenThrow(BadCredentialsException.class);

        Assertions.assertThrows(BadCredentialsException.class, () -> authService.login(testUser.getEmail(), password));
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser(Long id) {
        User user = new User("test@notexists.ru", "Alexey", "73454562345", Role.USER);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }
}

