package naumen.project.service;

import naumen.project.entity.User;
import naumen.project.exception.InvalidInputException;
import naumen.project.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param phone       новый телефон пользователя
     * @param email       новый email пользователя
     * @return обновленные данные пользователя
     */
    public User updateInfo(User updatedUser, String phone, String email) {
        if (phone != null) {
            Optional<User> userWithPhone = userRepository.findByPhone(phone);
            if (userWithPhone.isPresent() && !userWithPhone.get().getId().equals(updatedUser.getId())) {
                throw new InvalidInputException("Телефон уже занят");
            }

            updatedUser.setPhone(phone);
        }

        if (email != null) {
            Optional<User> userWithEmail = userRepository.findByEmail(email);
            if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(updatedUser.getId())) {
                throw new InvalidInputException("Email уже занят");
            }

            updatedUser.setEmail(email);
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
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Получить всех пользователей с пагинацией
     *
     * @param pageable параметры пагинации
     * @return страница пользователей
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
