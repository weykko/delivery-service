package naumen.project.dto.user;

/**
 * Ответ с информацией о пользователе
 */
public record UserResponseDto(
        Long id,
        String email,
        String name,
        String phone,
        String role
) {
}
