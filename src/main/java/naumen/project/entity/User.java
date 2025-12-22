package naumen.project.entity;

import jakarta.persistence.*;
import naumen.project.entity.enums.Role;

import java.util.List;

/**
 * Сущность пользователя системы.
 * Представляет общую сущность для клиентов, ресторанов и курьеров.
 *
 * @see Role
 */
@Entity
@Table(name = "\"user\"")
public class User extends IdEntity {
    /**
     * Email пользователя (используется для входа)
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Имя пользователя
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Телефон пользователя
     */
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    /**
     * Адрес пользователя
     */
    @Column(name = "address")
    private String address;

    /**
     * Роль пользователя в системе
     */
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Хэшированный пароль пользователя
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Список токенов пользователя
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AuthToken> authTokens;

    /**
     * Список позиций меню (только для ресторанов)
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MenuItem> menuItems;

    /**
     * Конструктор пользователя
     *
     * @param email   электронная почта пользователя
     * @param name    имя пользователя
     * @param phone   номер телефона пользователя
     * @param role    роль пользователя в системе
     * @param address адрес пользователя
     */
    public User(String email, String name, String phone, Role role, String address) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.address = address;
    }

    /**
     * Пустой конструктор для JPA.
     */
    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
