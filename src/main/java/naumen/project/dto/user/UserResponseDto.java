package naumen.project.dto.user;

import naumen.project.entity.enums.Role;

/**
 * Ответ с информацией о пользователе
 */
public record UserResponseDto(
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
         * Номер телефона пользователя
         */
        String phone,
        
        /**
         * Роль пользователя в системе
         */
        Role role
) {
}
