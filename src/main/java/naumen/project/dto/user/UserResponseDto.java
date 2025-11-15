package naumen.project.dto.user;

import naumen.project.entity.enums.Role;

/**
 * Ответ с информацией о пользователе
 *
 * @param id      Уникальный идентификатор пользователя
 * @param email   Электронная почта пользователя
 * @param role    Роль пользователя в системе
 * @param name    Имя пользователя
 * @param phone   Номер телефона пользователя
 * @param address Адрес пользователя
 */
public record UserResponseDto(

        Long id,

        String email,

        Role role,

        String name,

        String phone,

        String address
) {
}
