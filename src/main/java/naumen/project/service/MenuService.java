package naumen.project.service;

import naumen.project.dto.menu.UpdateMenuItemRequestDto;
import naumen.project.entity.MenuItem;
import naumen.project.entity.User;
import naumen.project.exception.EntityNotFoundException;
import naumen.project.exception.PermissionCheckFailedException;
import naumen.project.mapper.MenuMapper;
import naumen.project.mapper.PageMapper;
import naumen.project.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления меню ресторанов.
 * Обеспечивает операции создания, чтения, обновления и удаления позиций меню.
 *
 * @see MenuRepository
 * @see MenuMapper
 * @see PageMapper
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuService(MenuRepository menuRepository, MenuMapper menuMapper, PageMapper pageMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
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
     * Создает новую позицию в меню для указанного пользователя-ресторана.
     *
     * @param menuItem сущность позиции меню
     * @param user     пользователь-ресторан, для которого создается позиция
     * @return созданная позиция меню
     */
    public MenuItem createMenuItem(MenuItem menuItem, User user) {
        menuItem.setRestaurant(user);
        menuRepository.save(menuItem);

        return menuItem;
    }

    /**
     * Обновляет существующую позицию меню.
     *
     * @param id      идентификатор обновляемой позиции
     * @param request новые данные для позиции меню
     * @param user    пользователь, выполняющий обновление
     * @return обновленная позиция меню
     */
    public MenuItem updateMenuItem(Long id, UpdateMenuItemRequestDto request, User user) {
        MenuItem menuItem = getMenuItemById(id);

        assertBelongsToRestaurant(menuItem, user);

        menuMapper.updateEntityFromRequest(request, menuItem);
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
        MenuItem menuItem = getMenuItemById(id);

        assertBelongsToRestaurant(menuItem, user);

        menuRepository.delete(menuItem);
    }

    /**
     * Находит позицию меню по идентификатору.
     *
     * @param id идентификатор позиции меню
     * @return найденная позиция меню
     */
    public MenuItem getMenuItemById(Long id) {
        return menuRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Позиция меню с id '%d' не найдена", id));
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