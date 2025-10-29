package naumen.project.service;

import naumen.project.dto.user.UpdateUserRequestDto;
import naumen.project.dto.user.UserResponseDto;
import naumen.project.entity.User;
import naumen.project.exception.WebException;
import naumen.project.mapper.UserMapper;
import naumen.project.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Логика по работе с пользователями
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Инициализация, внедрение зависимостей
     */
    public UserService(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Возвращает информацию о переданном пользователе
     */
    public UserResponseDto getInfoForUser(User user) {
        return userMapper.toInfoDto(user);
    }

    /**
     * Обновление информации пользователя
     */
    @Transactional
    public UserResponseDto updateInfo(User user, UpdateUserRequestDto request) {
        User updatedUser = userMapper.updateUser(user, request);
        save(updatedUser);
        return userMapper.toInfoDto(updatedUser);
    }

    /**
     * Удалить пользователя
     */
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * Сохранить пользователя
     */
    private void save(User user) {
        userRepository.save(user);
    }
}
