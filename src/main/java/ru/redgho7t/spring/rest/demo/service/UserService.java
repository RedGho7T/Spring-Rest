package ru.redgho7t.spring.rest.demo.service;

import ru.redgho7t.spring.rest.demo.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User createUser(String firstName, String lastName, int age, String email,
                    String rawPassword, Set<Long> roleIds);

    User updateUser(Long id, String firstName, String lastName, int age, String email,
                    String rawPassword, Set<Long> roleIds);

    User registerNewUser(String name, int age, String email, String rawPassword);

    void deleteUser(Long id);
}