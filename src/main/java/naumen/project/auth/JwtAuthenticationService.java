package naumen.project.auth;

import naumen.project.entity.User;
import naumen.project.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtAuthenticationService {

    private final UserRepository userRepository;

    public JwtAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
