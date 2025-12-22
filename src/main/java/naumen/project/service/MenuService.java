package naumen.project.service;

import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.exception.InvalidInputException;
import naumen.project.exception.PermissionCheckFailedException;
import naumen.project.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для управления меню ресторанов.
 * Обеспечивает операции создания, чтения, обновления и удаления позиций меню.
 *
 * @see MenuRepository
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * Получает позиции меню с пагинацией и с возможностью фильтрации по ресторану и названию.
     *
     * @param restaurantId идентификатор ресторана для фильтрации
     * @param title        текст для поиска в названиях позиций меню
     * @param pageable     параметры пагинации
     * @return страница позиций меню
     */
    public Page<MenuItem> getMenuItems(Long restaurantId, String title, Pageable pageable) {
        return menuRepository
                .findByRestaurantIdAndTitle(restaurantId, title, pageable);
    }

    /**
     * Сохраняет новую позицию в меню
     *
     * @param menuItem сущность позиции меню
     * @return созданная позиция меню
     */
    public MenuItem save(MenuItem menuItem) {
        menuRepository.save(menuItem);

        return menuItem;
    }

    /**
     * Обновляет существующую позицию меню.
     *
     * @param menuItem позиция меню с обновленными данными
     * @param user     пользователь, выполняющий обновление
     * @return обновленная позиция меню
     */
    public MenuItem updateMenuItem(MenuItem menuItem, User user) {
        assertBelongsToRestaurant(menuItem, user);

        menuRepository.save(menuItem);

        return menuItem;
    }

    /**
     * Удаляет позицию меню.
     *
     * @param id   идентификатор удаляемой позиции
     * @param user пользователь, выполняющий удаление
     */
    public void deleteMenuItem(Long id, User user) {
        MenuItem menuItem = getMenuItemById(id)
                .orElseThrow(() -> new InvalidInputException(
                        "Не удалось удалить, причина: Позиция меню с id '%d' не найдена", id));

        assertBelongsToRestaurant(menuItem, user);

        menuRepository.delete(menuItem);
    }

    /**
     * Находит позицию меню по идентификатору.
     *
     * @param id идентификатор позиции меню
     * @return найденная позиция меню
     */
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuRepository.findById(id);
    }

    /**
     * Проверяет, принадлежит ли позиция меню указанному пользователю-ресторану.
     *
     * @param menuItem проверяемая позиция меню
     * @param user     пользователь для проверки принадлежности
     */
    private void assertBelongsToRestaurant(MenuItem menuItem, User user) {
        if (!menuItem.getRestaurant().getId().equals(user.getId())) {
            throw new PermissionCheckFailedException(
                    "Позиция меню с id '%d' не принадлежит вашему ресторану",
                    menuItem.getId());
        }
    }
}