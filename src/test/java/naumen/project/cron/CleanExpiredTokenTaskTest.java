package naumen.project.cron;

import naumen.project.repository.AuthTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для {@link CleanExpiredTokenTask}
 */
@ExtendWith(MockitoExtension.class)
class CleanExpiredTokenTaskTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private CleanExpiredTokenTask cleanExpiredTokenTask;

    @Test
    void run_ShouldCallRemoveAllExpiredWithCurrentTime() {
        cleanExpiredTokenTask.run();

        verify(authTokenRepository).removeAllExpired(any(Instant.class));
    }

    @Test
    void run_ShouldCallRepositoryExactlyOnce() {
        cleanExpiredTokenTask.run();

        verify(authTokenRepository, times(1)).removeAllExpired(any(Instant.class));
        verifyNoMoreInteractions(authTokenRepository);
    }

    @Test
    void run_ShouldExecuteSuccessfullyWithoutExceptions() {
        assertDoesNotThrow(() -> cleanExpiredTokenTask.run());
    }

    @Test
    void run_WhenRepositoryThrowsException_ShouldPropagateException() {
        doThrow(new RuntimeException("Database error"))
                .when(authTokenRepository).removeAllExpired(any(Instant.class));

        assertThrows(RuntimeException.class, () -> cleanExpiredTokenTask.run());
        verify(authTokenRepository).removeAllExpired(any(Instant.class));
    }

    @Test
    void run_ShouldPassCurrentInstantToRepository() {
        Instant[] capturedInstant = new Instant[1];
        doAnswer(invocation -> {
            capturedInstant[0] = invocation.getArgument(0);
            return null;
        }).when(authTokenRepository).removeAllExpired(any(Instant.class));

        cleanExpiredTokenTask.run();

        assertNotNull(capturedInstant[0]);
        assertTrue(Instant.now().minusSeconds(60).isBefore(capturedInstant[0]));
        assertTrue(Instant.now().plusSeconds(1).isAfter(capturedInstant[0]));
    }

    @Test
    void constructor_ShouldInitializeWithRepository() {
        AuthTokenRepository testRepository = mock(AuthTokenRepository.class);

        CleanExpiredTokenTask task = new CleanExpiredTokenTask(testRepository);

        assertNotNull(task);
    }
}
