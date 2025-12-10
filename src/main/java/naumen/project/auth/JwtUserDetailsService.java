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
 * Сервис для загрузки данных пользователя в контексте Spring Security
 *
 * @see UserRepository
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        return new JwtUserDetails(
                user,
                convertToAuthorities(user.getRole())
        );
    }

    /**
     * Получает пользователя по email
     *
     * @param email email пользователя
     * @return найденный пользователь
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Конвертирует роль пользователя в коллекцию прав доступа
     *
     * @param role роль пользователя
     * @return коллекция прав доступа на основе роли
     */
    private Collection<? extends GrantedAuthority> convertToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
