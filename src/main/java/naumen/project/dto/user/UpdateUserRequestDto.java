package naumen.project.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Обновление информации о пользователе
 */
public record UpdateUserRequestDto(
        /**
         * Имя пользователя (от 2 до 20 символов)
         */
        @Size(min = 2, max = 20)
        String name,

        /**
         * Номер телефона в формате +7XXXXXXXXXX или 8XXXXXXXXXX
         */
        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "номер должен начинаться с +7 или 8 и содержать только 11 цифр")
        String phone
) {
}
