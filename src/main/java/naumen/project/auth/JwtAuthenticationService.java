package naumen.project.auth;

import naumen.project.entity.User;
import naumen.project.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис по загрузке {@link JwtAuthentication}
 */
@Service
public class JwtAuthenticationService {

    private final UserRepository userRepository;

    /**
     * Инициализация сервиса
     */
    public JwtAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Получение {@link JwtAuthentication}
     */
    public JwtAuthentication getAuthentication(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        return new JwtAuthentication(
                user,
                true,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
