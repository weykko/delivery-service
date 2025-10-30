package naumen.project.dto.user;

import naumen.project.entity.enums.Role;

/**
 * Ответ с информацией о пользователе
 */
public record UserResponseDto(
        Long id,
        String email,
        String name,
        String phone,
        Role role
) {
}
