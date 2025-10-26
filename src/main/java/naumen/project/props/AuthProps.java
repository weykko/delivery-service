package naumen.project.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные проперти по токенам
 */
@ConfigurationProperties(prefix = "security.jwt")
public class AuthProps {

    private final TokenProps access = new TokenProps();
    private final TokenProps refresh = new TokenProps();

    public TokenProps getAccess() {
        return access;
    }

    public TokenProps getRefresh() {
        return refresh;
    }

    public class TokenProps {
        private String token;
        private long lifetime;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getLifetime() {
            return lifetime;
        }

        public void setLifetime(long lifetime) {
            this.lifetime = lifetime;
        }
    }
}
