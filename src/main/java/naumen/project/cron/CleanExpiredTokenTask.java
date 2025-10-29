package naumen.project.cron;

import naumen.project.repository.AuthTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Планировщик задач для очистки истекших токенов из базы данных.
 * Выполняет периодическое удаление просроченных JWT токенов для поддержания чистоты данных.
 *
 * @see AuthTokenRepository
 */
@Component
public class CleanExpiredTokenTask {

    private final AuthTokenRepository authTokenRepository;

    /**
     * Инициализация планировщика очистки токенов.
     */
    public CleanExpiredTokenTask(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    /**
     * Выполняет очистку истекших токенов из базы данных.
     * Метод запускается автоматически каждые 12 часов.
     */
    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void run() {
        authTokenRepository.removeAllExpired(Instant.now());
    }

}
