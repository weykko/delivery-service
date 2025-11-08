package naumen.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос для аутентификации
 */
public record LoginRequestDto(
        /**
         * Электронная почта пользователя
         */
        @Email
        @NotNull
        String email,

        /**
         * Пароль пользователя
         */
        @NotBlank
        String password
) {
}
