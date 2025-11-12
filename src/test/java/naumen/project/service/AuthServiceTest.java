package naumen.project.service;

import naumen.project.auth.JwtUserDetails;
import naumen.project.dto.auth.TokenResponseDto;
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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private AuthService authService;

    private final User testUser = createTestUser();

    /**
     * Тестирование успешной регистрации пользователя с валидными данными
     */
    @Test
    public void register_WithValidData_ShouldReturnSuccessResponse() {
        String password = "strongPassword";
        String encodedPassword = "encodedPassword";

        Mockito.when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByPhone(testUser.getPhone())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);

        User result = authService.register(testUser, password);

        Assertions.assertEquals(testUser.getEmail(), result.getEmail());
        Assertions.assertEquals(testUser.getRole(), result.getRole());
        Assertions.assertEquals(testUser.getName(), result.getName());
        Assertions.assertEquals(encodedPassword, result.getPassword());
        Mockito.verify(userRepository).existsByEmail(testUser.getEmail());
        Mockito.verify(userRepository).existsByPhone(testUser.getPhone());
        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(userRepository).save(testUser);
    }

    /**
     * Тестирование регистрации пользователя с уже существующим email
     */
    @Test
    public void register_WithExistingEmail_ShouldThrowWebException() {
        String password = "strongPassword";

        Mockito.when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> authService.register(testUser, password));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Email уже занят", exception.getMessage());
    }

    /**
     * Тестирование регистрации пользователя с уже существующим номером телефона
     */
    @Test
    public void register_WithExistingPhone_ShouldThrowWebException() {
        String password = "strongPassword";

        Mockito.when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByPhone(testUser.getPhone())).thenReturn(true);

        WebException exception = Assertions.assertThrows(WebException.class,
                () -> authService.register(testUser, password));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Телефон уже занят", exception.getMessage());
        Mockito.verify(userRepository).existsByEmail(testUser.getEmail());
    }

    /**
     * Тестирование успешного входа пользователя с валидными учетными данными
     */
    @Test
    public void login_WithValidCredentials_ShouldReturnTokens() {
        String password = "strongPassword";
        String aToken = "a";
        String rToken = "r";

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
    public void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        String password = "strongPassword";

        Mockito.when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(testUser.getEmail(), password))
        ).thenThrow(BadCredentialsException.class);

        Assertions.assertThrows(BadCredentialsException.class, () -> authService.login(testUser.getEmail(), password));
    }

    // Вспомогательные методы для создания тестовых данных

    /**
     * Создает тестового пользователя
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@notexists.ru");
        user.setName("Alexey");
        user.setPhone("73454562345");
        user.setRole(Role.USER);
        return user;
    }
}

