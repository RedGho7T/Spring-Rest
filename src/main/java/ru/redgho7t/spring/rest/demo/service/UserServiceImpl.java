package ru.redgho7t.spring.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.redgho7t.spring.rest.demo.dao.UserDao;
import ru.redgho7t.spring.rest.demo.model.Role;
import ru.redgho7t.spring.rest.demo.model.User;

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
    public UserServiceImpl(UserDao userDao, RoleService roleService, PasswordService passwordService) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.passwordService = passwordService;
        logger.info("UserService initialized with PasswordService (no circular dependencies)");
    }

    @Override
    public List<User> getAllUsers() {
        logger.debug("Fetching all users from database");
        return userDao.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.debug("Searching for user by ID: {}", id);
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);
        return userDao.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking if email exists: {}", email);
        return userDao.existsByEmail(email);
    }

    @Override
    public User createUser(String firstName, String lastName, int age,
                           String email, String rawPassword, Set<Long> roleIds) {
        logger.info("Creating new user: {}", email);

        if (existsByEmail(email)) {
            logger.warn("Attempt to create user with existing email: {}", email);
            throw new IllegalArgumentException("User with this email already exists");
        }

        validateUserData(firstName, lastName, age, email, rawPassword);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        String encodedPassword = passwordService.encodePassword(rawPassword);
        user.setPassword(encodedPassword);
        logger.debug("Password encrypted via PasswordService for user: {}", email);

        Set<Role> roles = resolveRoles(roleIds);
        user.setRoles(roles);

        userDao.save(user);
        logger.info("User {} successfully created with {} roles", email, roles.size());

        return user;
    }

    @Override
    public User updateUser(Long id, String firstName, String lastName,
                           int age, String email, String rawPassword, Set<Long> roleIds) {
        logger.info("Updating user with ID: {}", id);

        Optional<User> userOptional = getUserById(id);
        if (userOptional.isEmpty()) {
            logger.error("User with ID {} not found for update", id);
            throw new IllegalArgumentException("User not found");
        }

        User existingUser = userOptional.get();
        String oldEmail = existingUser.getEmail();

        if (!oldEmail.equals(email) && existsByEmail(email)) {
            logger.warn("Attempt to change email to existing one: {}", email);
            throw new IllegalArgumentException("User with this email already exists");
        }

        validateUserData(firstName, lastName, age, email, null);

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setAge(age);
        existingUser.setEmail(email);

        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            String encodedPassword = passwordService.encodePassword(rawPassword);
            existingUser.setPassword(encodedPassword);
            logger.debug("Password updated via PasswordService for user: {}", email);
        } else {
            logger.debug("Password not changed for user: {}", email);
        }

        Set<Role> roles = resolveRoles(roleIds);
        existingUser.setRoles(roles);

        userDao.update(existingUser);
        logger.info("User {} successfully updated", email);

        return existingUser;
    }

    @Override
    public User registerNewUser(String name, int age, String email, String rawPassword) {
        logger.info("Registering new user: {}", email);

        Role userRole = roleService.getRoleByName("ROLE_USER");
        if (userRole == null) {
            logger.error("ROLE_USER not found in system");
            throw new IllegalStateException("User role not found");
        }

        Set<Long> userRoleIds = Set.of(userRole.getId());

        String[] nameParts = name.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : firstName;

        return createUser(firstName, lastName, age, email, rawPassword, userRoleIds);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        Optional<User> userOptional = getUserById(id);
        if (userOptional.isEmpty()) {
            logger.warn("Attempt to delete non-existing user with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        userDao.deleteById(id);
        logger.info("User {} successfully deleted", user.getEmail());
    }

    private void validateUserData(String firstName, String lastName, int age, String email, String password) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (age < 1 || age > 150) {
            throw new IllegalArgumentException("Age must be between 1 and 150");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (password != null && password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>();

        if (roleIds == null || roleIds.isEmpty()) {
            Role defaultRole = roleService.getRoleByName("ROLE_USER");
            if (defaultRole != null) {
                roles.add(defaultRole);
                logger.debug("Assigned default role: ROLE_USER");
            }
        } else {
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                if (role != null) {
                    roles.add(role);
                    logger.debug("Added role: {}", role.getName());
                } else {
                    logger.warn("Role with ID {} not found", roleId);
                }
            }
        }

        if (roles.isEmpty()) {
            logger.error("Failed to assign any roles to user");
            throw new IllegalStateException("User must have at least one role");
        }

        return roles;
    }
}
