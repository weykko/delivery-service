package naumen.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос для аутентификации
 */
public record LoginRequestDto(
        @Email
        @NotNull
        String email,

        @NotBlank
        String password
) {
}
