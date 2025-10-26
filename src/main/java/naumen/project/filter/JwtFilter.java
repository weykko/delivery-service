package naumen.project.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import naumen.project.auth.JwtAuthenticationService;
import naumen.project.entity.enums.TokenType;
import naumen.project.service.AuthTokenService;
import naumen.project.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр, отвечающий за авторизацию запроса
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";

    private final JwtUtil jwtUtil;
    private final AuthTokenService authTokenService;
    private final JwtAuthenticationService jwtAuthenticationService;

    public JwtFilter(
            JwtUtil jwtUtil,
            AuthTokenService authTokenService,
            JwtAuthenticationService jwtAuthenticationService
    ) {
        this.jwtUtil = jwtUtil;
        this.authTokenService = authTokenService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);
        if (isTokenValid(token)) {
            Long userId = jwtUtil.extractAccessUserId(token);
            Authentication authentication = jwtAuthenticationService.getAuthentication(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_NAME);
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return token.substring(BEARER_PREFIX.length());
    }

    private boolean isTokenValid(String token) {
        return token != null
               && jwtUtil.validateAccessToken(token)
               && authTokenService.isTokenAllowed(token, TokenType.ACCESS);
    }
}
