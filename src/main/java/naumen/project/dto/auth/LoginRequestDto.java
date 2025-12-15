package naumen.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Запрос для аутентификации
 *
 * @param email    электронная почта пользователя
 * @param password пароль пользователя
 */
public record LoginRequestDto(

        @Email
        @NotNull
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 255)
        String password
) {
}
