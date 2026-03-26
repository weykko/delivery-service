package naumen.project.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import naumen.project.entity.enums.Role;

/**
 * Обновление информации о пользователе администратором
 *
 * @param email   электронная почта пользователя
 * @param role    роль пользователя в системе
 * @param name    имя пользователя (от 2 до 20 символов)
 * @param phone   номер телефона пользователя (должен начинаться с +7 или 8 и содержать 11 цифр)
 * @param address адрес пользователя (максимальная длина 255 символов)
 */
public record AdminUpdateUserRequestDto(

        @Email
        @Size(max = 255)
        String email,

        Role role,

        @Size(min = 2, max = 20)
        String name,

        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "номер должен начинаться с +7 или 8 и содержать только 11 цифр")
        String phone,

        @Size(max = 255)
        String address
) {
}
