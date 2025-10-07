package ru.redgho7t.spring.rest.demo.service;

import ru.redgho7t.spring.rest.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    boolean existsByEmail(String email);

    void deleteUser(Long id);

    User createUser(String firstName, String lastName, Integer age, String email,
                    String rawPassword, Set<Long> roleIds);

    User updateUser(Long id, String firstName, String lastName, Integer age, String email,
                    String rawPassword, Set<Long> roleIds);

    User registerUser(String firstName, String lastName, Integer age, String email, String rawPassword);
}