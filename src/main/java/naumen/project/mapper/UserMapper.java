package naumen.project.mapper;

import naumen.project.dto.auth.RegisterResponseDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import org.mapstruct.Mapper;

/**
 * Маппер для преобразования между сущностью User и DTO.
 * Обеспечивает маппинг данных пользователя для регистрации, обновления и отображения.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует сущность User в DTO ответа.
     *
     * @param user сущность пользователя
     * @return DTO с данными пользователя
     */
    UserResponseDto toResponse(User user);

    /**
     * Преобразует сущность User в DTO ответа регистрации.
     *
     * @param user сущность пользователя
     * @return DTO с данными зарегистрированного пользователя
     */
    RegisterResponseDto toRegisterResponse(User user);
}
