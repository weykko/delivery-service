package naumen.project.mapper;

import naumen.project.dto.auth.RegisterRequestDto;
import naumen.project.dto.auth.RegisterResponseDto;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Маппер для пользователей
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toResponse(User user);

    User updateUserEntityFromRequest(UpdateUserRequestDto request, @MappingTarget User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequestDto request);

    RegisterResponseDto toRegisterResponse(User user);
}

//public class UserMapper {
//
//    /**
//     * Замапить в dto с информацией пользователя
//     */
//    public UserResponseDto toInfoDto(User user) {
//        return new UserResponseDto(
//                user.getId(),
//                user.getEmail(),
//                user.getName(),
//                user.getPhone(),
//                user.getRole().name()
//        );
//    }
//
//    /**
//     * Обновить поля пользователя
//     */
//    public User updateUser(User user, UpdateUserRequestDto dto) {
//        user.setName(dto.name());
//        user.setPhone(dto.phone());
//        return user;
//    }
//
//    public User toUser(RegisterRequestDto dto, String password) {
//        User user = new User();
//        user.setEmail(dto.email());
//        user.setName(dto.name());
//        user.setPhone(dto.phone());
//        user.setRole(dto.role());
//        user.setPassword(password);
//        return user;
//    }
//
//    public RegisterResponseDto toRegisterResponse(User user) {
//        return new RegisterResponseDto(
//                user.getId(),
//                user.getEmail(),
//                user.getName(),
//                user.getRole()
//        );
//    }
//}
