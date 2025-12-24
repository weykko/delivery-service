package naumen.project.service;

import naumen.project.entity.User;
import naumen.project.exception.InvalidInputException;
import naumen.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Сервис для операций с пользователями.
 * Предоставляет методы для получения, обновления и удаления пользователей.
 *
 * @see UserRepository
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
    public User updateInfo(User updatedUser) {
        Optional<User> userWithPhone = userRepository.findByPhone(updatedUser.getPhone());
        if (userWithPhone.isPresent() && !userWithPhone.get().getId().equals(updatedUser.getId())) {
            throw new InvalidInputException("Телефон уже занят");
        }

        saveUser(updatedUser);
        return updatedUser;
    }

    /**
     * Удаляет переданного пользователя.
     *
     * @param user пользователь
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * Сохранить пользователя в БД
     *
     * @param user пользователь
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Выполняет проверку на уникальность полей запроса.
     *
     * @param user пользователь
     */
    public void checkUniqueFieldsRegistration(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new InvalidInputException("Email уже занят");
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new InvalidInputException("Телефон уже занят");
        }
    }

    /**
     * Получить пользователя по id
     *
     * @param id id пользователя
     * @return пользователь
     */
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }
}
