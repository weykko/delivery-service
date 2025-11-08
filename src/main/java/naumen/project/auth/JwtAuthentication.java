package naumen.project.auth;

import naumen.project.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Реализация аутентификации на основе JWT токена
 */
public class JwtAuthentication implements Authentication {

    private User user;
    private boolean authenticated;
    private List<GrantedAuthority> authorities;

    public JwtAuthentication(User user, boolean authenticated, List<GrantedAuthority> authorities) {
        this.user = user;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return null;
    }
}
