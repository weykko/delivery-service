package naumen.project.service;

import naumen.project.auth.JwtUserDetails;
import naumen.project.dto.auth.LoginRequestDto;
import naumen.project.dto.auth.RegisterRequestDto;
import naumen.project.dto.auth.RegisterResponseDto;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.exception.WebException;
import naumen.project.mapper.UserMapper;
import naumen.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Тесты, связанные с регистрацией и аутентификацией пользователя
 */
public class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AuthTokenService authTokenService = mock(AuthTokenService.class);

    private final AuthService authService = new AuthService(
            userRepository,
            userMapper,
            passwordEncoder,
            authenticationManager,
            authTokenService
    );

    /**
     * Тестируем, что регистрация работает корректно
     */
    @Test
    public void testCorrectRegister() {
        String email = "test@notexists@ru";
        String password = "strongPassword";
        Role role = Role.ROLE_USER;
        String name = "Alexey";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                password,
                Role.ROLE_USER,
                name,
                phone
        );

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setName(name);
        user.setPhone(phone);

        long userId = 1;
        user.setId(userId);

        RegisterResponseDto response = new RegisterResponseDto(
                userId,
                email,
                name,
                role
        );

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userMapper.toUser(request, password)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toRegisterResponse(user)).thenReturn(response);

        RegisterResponseDto result = authService.register(request);
        assertEquals(request.email(), result.email());
        assertEquals(request.role(), result.role());
        assertEquals(userId, result.id());
        assertEquals(request.name(), result.name());
    }

    /**
     * Проверяем, что выбрасывается нужное исключение при дублировании email
     */
    @Test
    public void testEmailAlreadyExistsRegister() {
        String email = "test@notexists@ru";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                "strongPassword",
                Role.ROLE_USER,
                "Alexey",
                phone
        );

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.existsByPhone(phone)).thenReturn(false);

        assertThrows(WebException.class, () -> authService.register(request));
    }

    /**
     * Проверяем, что выбрасывается нужное исключение при дублировании phone
     */
    @Test
    public void testPhoneAlreadyExistsRegister() {
        String email = "test@notexists@ru";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                "strongPassword",
                Role.ROLE_USER,
                "Alexey",
                phone
        );

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(true);

        assertThrows(WebException.class, () -> authService.register(request));
    }

    /**
     * Тестируем корректный вход
     */
    @Test
    public void testLoginCorrect() {
        String email = "test@notexists@ru";
        String password = "strongPassword";

        LoginRequestDto request = new LoginRequestDto(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.ROLE_USER);
        user.setName("Name");
        user.setPhone("73454562345");
        user.setId(1L);

        String aToken = "a";
        String rToken = "r";

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()))
        ).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserDetails(user, null),
                        null
                )
        );
        when(authTokenService.generateAndSave(user)).thenReturn(new TokenResponseDto(aToken, rToken));

        TokenResponseDto result = authService.login(request);
        assertAll(
                () -> assertEquals(aToken, result.accessToken()),
                () -> assertEquals(rToken, result.refreshToken())
        );
    }


    /**
     * Тестируем неверные данные для логина
     */
    @Test
    public void testLoginWrong() {
        String email = "test@notexists@ru";
        String password = "strongPassword";

        LoginRequestDto request = new LoginRequestDto(email, password);

        when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()))
        ).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

}
