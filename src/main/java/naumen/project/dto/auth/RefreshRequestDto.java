package naumen.project.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос по обновлению токенов
 */
public record RefreshRequestDto(
        @NotBlank
        String refreshToken
) {
}
