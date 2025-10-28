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
     * Получить информацию о пользователе по id
     */
    @Transactional(readOnly = true)
    public UserResponseDto getInfoById(Long id) {
        User user = getById(id);
        return userMapper.toResponse(user);
    }

    /**
     * Обновить информацию о пользователе по id
     */
    @Transactional
    public UserResponseDto updateInfoById(Long id, UpdateUserRequestDto request) {
        User user = getById(id);
        User updatedUser = userMapper.updateUserEntityFromRequest(request, user);
        save(updatedUser);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Удалить пользователя по id
     */
    @Transactional
    public void deleteById(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    /**
     * Получить пользователя из бд
     */
    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new WebException(HttpStatus.NOT_FOUND, "Пользователь с id '%d' не найден", id)
                );
    }

    /**
     * Сохранить пользователя
     */
    private void save(User user) {
        userRepository.save(user);
    }
}
