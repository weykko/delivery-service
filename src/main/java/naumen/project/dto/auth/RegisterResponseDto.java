package naumen.project.dto.auth;

import naumen.project.entity.enums.Role;

/**
 * Ответ при регистрации
 */
public record RegisterResponseDto(
        Long id,
        String email,
        String name,
        Role role
) {
}
