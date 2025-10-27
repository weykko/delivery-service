package naumen.project.dto.auth;

/**
 * Ответ с токенами
 */
public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
