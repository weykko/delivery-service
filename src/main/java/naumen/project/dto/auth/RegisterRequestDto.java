package naumen.project.dto.auth;

import jakarta.validation.constraints.*;
import naumen.project.entity.enums.Role;

/**
 * Запрос на регистрацию
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

        @NotNull
        @Size(max = 255)
        String name,

        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "номер должен начинаться с +7 или 8 и содержать только 11 цифер")
        @NotNull
        String phone
) {
}
