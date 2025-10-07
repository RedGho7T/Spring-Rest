package ru.redgho7t.spring.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.redgho7t.spring.rest.demo.dao.UserDao;
import ru.redgho7t.spring.rest.demo.model.Role;
import ru.redgho7t.spring.rest.demo.model.User;
import ru.redgho7t.spring.rest.demo.service.PasswordService;
import ru.redgho7t.spring.rest.demo.service.RoleService;
import ru.redgho7t.spring.rest.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordService passwordService;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           RoleService roleService,
                           PasswordService passwordService) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.passwordService = passwordService;
        logger.info("UserService initialized");
    }

    @Override
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        return userDao.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.debug("Fetch user by ID: {}", id);
        return userDao.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Fetch user by email: {}", email);
        return userDao.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Check exists email: {}", email);
        return userDao.existsByEmail(email);
    }

    @Override
    public User createUser(String firstName,
                           String lastName,
                           Integer age,
                           String email,
                           String rawPassword,
                           Set<Long> roleIds) {
        logger.info("Create user: {}", email);

        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        validateUserData(firstName, lastName, age, email, rawPassword);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(passwordService.encode(rawPassword));

        String fullName = generateFullName(firstName, lastName);
        user.setName(fullName);
        logger.debug("Generated name for user {}: '{}'", email, fullName);

        user.setRoles(resolveRoles(roleIds));

        userDao.save(user);
        return user;
    }

    @Override
    public User updateUser(Long id,
                           String firstName,
                           String lastName,
                           Integer age,
                           String email,
                           String rawPassword,
                           Set<Long> roleIds) {
        logger.info("Update user: {}", id);

        User user = getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(email) && existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        validateUserData(firstName, lastName, age, email, rawPassword);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        user.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));

        String fullName = generateFullName(firstName, lastName);
        user.setName(fullName);
        logger.debug("Updated name for user {}: '{}'", email, fullName);

        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordService.encode(rawPassword));
        }

        user.setRoles(resolveRoles(roleIds));

        userDao.update(user);
        return user;
    }

    @Override
    public User registerUser(String firstName,
                             String lastName,
                             Integer age,
                             String email,
                             String rawPassword) {
        logger.info("Register user: {}", email);

        Role userRole = roleService.getRoleByName("ROLE_USER");
        Set<Long> roleIds = Set.of(userRole.getId());

        return createUser(firstName, lastName, age, email, rawPassword, roleIds);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Delete user: {}", id);

        if (getUserById(id).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        userDao.deleteById(id);
    }

    private String generateFullName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return "Unknown User";
        }

        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";

        if (first.isEmpty() && last.isEmpty()) {
            return "Unknown User";
        }

        if (first.isEmpty()) {
            return last;
        }

        if (last.isEmpty()) {
            return first;
        }

        return first + " " + last;
    }

    private void validateUserData(String firstName,
                                  String lastName,
                                  Integer age,
                                  String email,
                                  String password) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name required");
        }
        if (age == null || age < 1 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password != null && password.length() < 3) {
            throw new IllegalArgumentException("Password too short");
        }
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>();

        if (roleIds == null || roleIds.isEmpty()) {
            roles.add(roleService.getRoleByName("ROLE_USER"));
        } else {
            for (Long rid : roleIds) {
                Role r = roleService.getRoleById(rid);
                if (r != null) {
                    roles.add(r);
                }
            }
        }

        if (roles.isEmpty()) {
            throw new IllegalStateException("User must have roles");
        }

        return roles;
    }
}