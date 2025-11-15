package naumen.project.dto.auth;

import naumen.project.entity.enums.Role;

/**
 * Ответ при регистрации
 *
 * @param id    уникальный идентификатор пользователя
 * @param email электронная почта пользователя
 * @param role  роль пользователя в системе
 * @param name  имя пользователя
 */
public record RegisterResponseDto(

        Long id,

        String email,

        Role role,

        String name
) {
}
