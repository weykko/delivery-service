package naumen.project.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Обновление информации о пользователе
 */
public record UpdateUserRequestDto(
        @Size(max = 255)
        @NotBlank
        String name,

        @Size(min = 11, max = 11, message = "длина должна равняться 11")
        @Pattern(regexp = "^\\d+$", message = "должны быть только цифры")
        String phone
) {
}
