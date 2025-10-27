package naumen.project.cron;

import naumen.project.repository.AuthTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Cron по очистке истекших токенов из бд
 */
@Component
public class CleanExpiredTokenTask {

    private final AuthTokenRepository authTokenRepository;

    /**
     * Инициализация крона
     */
    public CleanExpiredTokenTask(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    /**
     * Метод, вызывающийся раз в 12 часов
     */
    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void run() {
        authTokenRepository.removeAllExpired(Instant.now());
    }

}
