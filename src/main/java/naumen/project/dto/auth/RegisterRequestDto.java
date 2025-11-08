package naumen.project.dto.auth;

import jakarta.validation.constraints.*;
import naumen.project.entity.enums.Role;

/**
 * Запрос на регистрацию
 */
public record RegisterRequestDto(

        /**
         * Электронная почта пользователя (максимум 255 символов)
         */
        @Email
        @NotNull
        @Size(max = 255)
        String email,

        /**
         * Пароль пользователя (минимум 8 символов, максимум 255)
         */
        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        /**
         * Роль пользователя в системе
         */
        @NotNull
        Role role,

        /**
         * Имя пользователя (от 2 до 20 символов)
         */
        @NotBlank
        @Size(min = 2, max = 20)
        String name,

        /**
         * Номер телефона в формате +7XXXXXXXXXX или 8XXXXXXXXXX
         */
        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "номер должен начинаться с +7 или 8 и содержать только 11 цифр")
        @NotNull
        String phone
) {
}
