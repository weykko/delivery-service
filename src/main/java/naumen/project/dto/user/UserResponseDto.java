package naumen.project.dto.user;

import naumen.project.entity.enums.Role;

/**
 * Ответ с информацией о пользователе
 *
 * @param id    Уникальный идентификатор пользователя
 * @param email Электронная почта пользователя
 * @param name  Имя пользователя
 * @param phone Номер телефона пользователя
 * @param role  Роль пользователя в системе
 */
public record UserResponseDto(

        Long id,

        String email,

        String name,

        String phone,

        Role role
) {
}
