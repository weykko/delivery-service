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
 * Сервис для операций с пользователями.
 * Предоставляет методы для получения, обновления и удаления пользователей.
 *
 * @see UserRepository
 * @see UserMapper
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
     * Получает информацию о переданном пользователе.
     *
     * @param user пользователь
     * @return данные пользователя
     */
    @Transactional(readOnly = true)
    public UserResponseDto getInfoById(Long id) {
        User user = getById(id);
        return userMapper.toResponse(user);
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param user пользователь
     * @param request новые данные пользователя
     * @return обновленные данные пользователя
     */
    @Transactional
    public UserResponseDto updateInfoById(Long id, UpdateUserRequestDto request) {
        User user = getById(id);
        User updatedUser = userMapper.updateUserEntityFromRequest(request, user);
        save(updatedUser);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Удаляет переданного пользователя.
     *
     * @param user пользователь
     */
    @Transactional
    public void deleteById(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    /**
     * Находит пользователя в базе данных по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     */
    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new WebException(HttpStatus.NOT_FOUND, "Пользователь с id '%d' не найден", id)
                );
    }

    /**
     * Сохраняет пользователя в базе данных.
     *
     * @param user сохраняемый пользователь
     */
    private void save(User user) {
        userRepository.save(user);
    }
}
