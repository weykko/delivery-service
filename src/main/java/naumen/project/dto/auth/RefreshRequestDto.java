package naumen.project.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос по обновлению токенов
 *
 * @param refreshToken Refresh токен для обновления пары токенов
 */
public record RefreshRequestDto(

        @NotBlank
        String refreshToken
) {
}
