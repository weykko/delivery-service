package naumen.project.mapper;

import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import org.springframework.stereotype.Component;

/**
 * Маппер для пользователей
 */
@Component
public class UserMapper {

    /**
     * Замапить в dto с информацией пользователя
     */
    public UserResponseDto toInfoDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    /**
     * Обновить поля пользователя
     */
    public User updateUser(User user, UpdateUserRequestDto dto) {
        user.setName(dto.name());
        user.setPhone(dto.phone());
        return user;
    }
}
