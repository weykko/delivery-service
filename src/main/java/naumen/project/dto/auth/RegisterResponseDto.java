package naumen.project.dto.auth;

import naumen.project.entity.enums.Role;

/**
 * Ответ при регистрации
 */
public record RegisterResponseDto(
        /**
         * Уникальный идентификатор пользователя
         */
        Long id,
        
        /**
         * Электронная почта пользователя
         */
        String email,
        
        /**
         * Имя пользователя
         */
        String name,
        
        /**
         * Роль пользователя в системе
         */
        Role role
) {
}
