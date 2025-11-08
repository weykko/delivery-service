package naumen.project.dto.auth;

import naumen.project.entity.enums.Role;

/**
 * Ответ при регистрации
 *
 * @param id    уникальный идентификатор пользователя
 * @param email электронная почта пользователя
 * @param name  имя пользователя
 * @param role  роль пользователя в системе
 */
public record RegisterResponseDto(

        Long id,

        String email,

        String name,

        Role role
) {
}
