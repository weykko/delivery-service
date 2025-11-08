package naumen.project.dto.auth;

/**
 * Ответ с токенами
 */
public record TokenResponseDto(
        /**
         * Access токен для авторизации запросов
         */
        String accessToken,
        
        /**
         * Refresh токен для обновления пары токенов
         */
        String refreshToken
) {
}
