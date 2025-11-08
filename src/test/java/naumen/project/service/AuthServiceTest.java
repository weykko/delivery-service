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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты для {@link AuthService}
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void register_WithValidData_ShouldReturnSuccessResponse() {
        String email = "test@notexists@ru";
        String password = "strongPassword";
        Role role = Role.USER;
        String name = "Alexey";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                password,
                Role.USER,
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
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toRegisterResponse(user)).thenReturn(response);

        RegisterResponseDto result = authService.register(request);
        assertEquals(request.email(), result.email());
        assertEquals(request.role(), result.role());
        assertEquals(userId, result.id());
        assertEquals(request.name(), result.name());
    }

    @Test
    public void register_WithExistingEmail_ShouldThrowWebException() {
        String email = "test@notexists@ru";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                "strongPassword",
                Role.USER,
                "Alexey",
                phone
        );

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(WebException.class, () -> authService.register(request));
    }

    @Test
    public void register_WithExistingPhone_ShouldThrowWebException() {
        String email = "test@notexists@ru";
        String phone = "73454562345";

        RegisterRequestDto request = new RegisterRequestDto(
                email,
                "strongPassword",
                Role.USER,
                "Alexey",
                phone
        );

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(true);

        assertThrows(WebException.class, () -> authService.register(request));
    }

    @Test
    public void login_WithValidCredentials_ShouldReturnTokens() {
        String email = "test@notexists@ru";
        String password = "strongPassword";

        LoginRequestDto request = new LoginRequestDto(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.USER);
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

    @Test
    public void login_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        String email = "test@notexists@ru";
        String password = "strongPassword";

        LoginRequestDto request = new LoginRequestDto(email, password);

        when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()))
        ).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
