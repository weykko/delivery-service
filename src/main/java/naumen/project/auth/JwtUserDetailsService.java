package naumen.project.auth;

import naumen.project.entity.User;
import naumen.project.entity.enums.Role;
import naumen.project.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Сервис загрузки {@link JwtUserDetails}
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружаем {@link JwtUserDetails}
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        return new JwtUserDetails(
                user,
                convertToAuthorities(user.getRole())
        );
    }

    /**
     * Получить пользователя по почте
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Преобразовать роль в authorities
     */
    private Collection<? extends GrantedAuthority> convertToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
