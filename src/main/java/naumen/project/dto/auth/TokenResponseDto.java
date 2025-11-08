package naumen.project.dto.auth;

/**
 * Ответ с токенами
 *
 * @param accessToken  Access токен для авторизации запросов
 * @param refreshToken Refresh токен для обновления пары токенов
 */
public record TokenResponseDto(

        String accessToken,

        String refreshToken
) {
}
