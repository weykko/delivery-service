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

        @Pattern(regexp = "^\\d+$", message = "должны быть только цифры")
        @Size(min = 11, max = 11, message = "длина должна равняться 11")
        @NotNull
        String phone
) {
}
