package naumen.project.dto.auth;

import jakarta.validation.constraints.*;
import naumen.project.entity.enums.Role;

/**
 * Запрос на регистрацию
 *
 * @param email    электронная почта пользователя
 * @param password пароль пользователя (от 8 до 255 символов)
 * @param role     роль пользователя в системе
 * @param name     имя пользователя (от 2 до 20 символов)
 * @param phone    номер телефона пользователя (должен начинаться с +7 или 8 и содержать 11 цифр)
 * @param address  адрес пользователя (максимальная длина 255 символов)
 */
public record RegisterRequestDto(

        @Email
        @NotNull
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        @NotNull
        Role role,

        @NotBlank
        @Size(min = 2, max = 20)
        String name,

        @NotNull
        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "номер должен начинаться с +7 или 8 и содержать только 11 цифр")
        String phone,

        @Size(max = 255)
        String address
) {
}
