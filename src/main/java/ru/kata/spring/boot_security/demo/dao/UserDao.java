package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

/**
 * DAO интерфейс для работы с пользователями в базе данных
 * Содержит все методы для CRUD операций с User
 */
public interface UserDao {

    /**
     * Получить всех пользователей
     */
    List<User> findAll();

    /**
     * Найти пользователя по ID
     */
    User findById(Long id);

    /**
     * Сохранить нового пользователя
     */
    void save(User user);

    /**
     * Обновить существующего пользователя
     */
    void update(User user);

    /**
     * Удалить пользователя по ID
     */
    void delete(Long id);

    /**
     * Удалить пользователя по ID (альтернативное название)
     */
    void deleteById(Long id);

    /**
     * Найти пользователей по части имени
     */
    List<User> findByNameContaining(String name);

    /**
     * Проверить, существует ли пользователь с данным email
     */
    boolean existsByEmail(String email);

    /**
     * Найти пользователя по email
     * Главный метод для поиска по email
     */
    User findByEmail(String email);

    User getByEmail(String email);
}