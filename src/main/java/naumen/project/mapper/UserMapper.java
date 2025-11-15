package naumen.project.mapper;

import naumen.project.dto.auth.RegisterRequestDto;
import naumen.project.dto.auth.RegisterResponseDto;
import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import org.mapstruct.*;

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
     * Обновляет сущность User данными из DTO запроса.
     *
     * @param request DTO с обновленными данными пользователя
     * @param user сущность для обновления
     * @return обновленная сущность пользователя
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserEntityFromRequest(UpdateUserRequestDto request, @MappingTarget User user);

    /**
     * Преобразует DTO регистрации в сущность User.
     *
     * @param request DTO с данными для регистрации
     * @return сущность пользователя
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "address", source = "address")
    User toEntity(RegisterRequestDto request);

    /**
     * Преобразует сущность User в DTO ответа регистрации.
     *
     * @param user сущность пользователя
     * @return DTO с данными зарегистрированного пользователя
     */
    RegisterResponseDto toRegisterResponse(User user);
}
