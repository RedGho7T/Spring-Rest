package ru.redgho7t.spring.rest.demo.dao;

import ru.redgho7t.spring.rest.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> findAll();

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}