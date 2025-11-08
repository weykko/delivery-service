package naumen.project.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос по обновлению токенов
 */
public record RefreshRequestDto(
        /**
         * Refresh токен для обновления пары токенов
         */
        @NotBlank
        String refreshToken
) {
}
