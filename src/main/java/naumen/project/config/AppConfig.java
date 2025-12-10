package naumen.project.config;

import naumen.project.auth.AuthProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Общие настройки приложения
 */
@EnableConfigurationProperties({AuthProps.class})
@EnableScheduling
@Configuration
public class AppConfig {
}
