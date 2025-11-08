package naumen.project.service;

import naumen.project.auth.JwtUserDetails;
import naumen.project.dto.auth.TokenResponseDto;
import naumen.project.entity.User;
import naumen.project.exception.WebException;
import naumen.project.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки операций аутентификации и регистрации пользователей.
 * Отвечает за регистрацию новых пользователей и их вход в систему.
 *
 * @see AuthTokenService
 */
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;

    public AuthService(
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            AuthTokenService authTokenService,
            UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.authTokenService = authTokenService;
        this.userRepository = userRepository;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param user     регистрируемый пользователь
     * @param password пароль пользователя
     * @return зарегистрированный пользователь
     */
    public User register(User user, String password) {
        checkUniqueFieldsRegistration(user);
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);

        userRepository.save(user);

        return user;
    }

    /**
     * Выполняет аутентификацию пользователя и генерирует токены.
     *
     * @param email    почта пользователя
     * @param password пароль пользователя
     * @return сгенерированные access и refresh токены
     */
    public TokenResponseDto login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();

        return authTokenService.generateAndSave(userDetails.getUser());
    }

    /**
     * Выполняет проверку на уникальность полей запроса.
     *
     * @param user пользователь
     */
    public void checkUniqueFieldsRegistration(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Email уже занят");
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Телефон уже занят");
        }
    }
}
