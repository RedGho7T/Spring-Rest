package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

/**
 * Сервис для работы с пользователями
 * Содержит бизнес-логику управления пользователями
 */
public interface UserService {

    /**
     * Получить всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Найти пользователя по ID
     */
    User getUserById(Long id);

    /**
     * Сохранить нового пользователя
     */
    void saveUser(User user);

    /**
     * Обновить существующего пользователя
     */
    void updateUser(User user);

    /**
     * Удалить пользователя по ID
     */
    void deleteUser(Long id);

    /**
     * Найти пользователей по имени
     */
    List<User> findByName(String name);

    /**
     * Проверить, существует ли пользователь с данным email
     */
    boolean existsByEmail(String email);

    /**
     * Найти пользователя по email (оригинальное имя метода)
     * Используется в оригинальном коде
     */
    User getByEmail(String email);

    /**
     * Найти пользователя по email (новое имя метода)
     * Используется в новом коде регистрации
     */
    User findByEmail(String email);
}