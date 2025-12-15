package naumen.project.cron;

import naumen.project.repository.AuthTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;


/**
 * Модульные тесты для {@link CleanExpiredTokenTask}
 */
@ExtendWith(MockitoExtension.class)
class CleanExpiredTokenTaskTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private CleanExpiredTokenTask cleanExpiredTokenTask;

    /**
     * Тестирование вызова метода удаления просроченных токенов и передачи текущего времени в репозиторий
     */
    @Test
    void runShouldPassCurrentInstantToRepository() {
        Instant[] capturedInstant = new Instant[1];
        Mockito.doAnswer(invocation -> {
            capturedInstant[0] = invocation.getArgument(0);
            return null;
        }).when(authTokenRepository).removeAllExpired(ArgumentMatchers.any(Instant.class));

        cleanExpiredTokenTask.run();

        Assertions.assertNotNull(capturedInstant[0]);
        Assertions.assertTrue(Instant.now().minusSeconds(60).isBefore(capturedInstant[0]));
        Assertions.assertTrue(Instant.now().plusSeconds(1).isAfter(capturedInstant[0]));

        Mockito.verify(authTokenRepository).removeAllExpired(ArgumentMatchers.any(Instant.class));
    }
}
