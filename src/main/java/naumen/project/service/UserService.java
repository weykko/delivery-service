package naumen.project.service;

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

    /**
     * Инициализация, внедрение зависимостей
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param updatedUser пользователь с обновленными данными
     * @return обновленные данные пользователя
     */
    @Transactional
    public User updateInfo(User updatedUser) {
        if (userRepository.countByPhone(updatedUser.getPhone()) > 1) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Телефон уже занят");
        }

        save(updatedUser);
        return updatedUser;
    }

    /**
     * Удаляет переданного пользователя.
     *
     * @param user пользователь
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
