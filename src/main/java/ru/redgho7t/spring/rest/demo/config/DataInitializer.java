package ru.redgho7t.spring.rest.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.redgho7t.spring.rest.demo.dao.RoleDao;
import ru.redgho7t.spring.rest.demo.dao.UserDao;
import ru.redgho7t.spring.rest.demo.model.Role;
import ru.redgho7t.spring.rest.demo.model.User;
import ru.redgho7t.spring.rest.demo.service.PasswordService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordService passwordService;

    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    private static final String DEFAULT_USER_PASSWORD = "user";
    private static final String DEFAULT_TEST_PASSWORD = "test";

    @Autowired
    public DataInitializer(UserDao userDao, RoleDao roleDao, @Lazy PasswordService passwordService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordService = passwordService;
        logger.info("DataInitializer initialized");
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting data initialization");

        try {
            Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
            Role userRole = createRoleIfNotExists("ROLE_USER");

            createUserIfNotExists(
                    "Admin", "Administrator", 30, "admin@admin.com",
                    DEFAULT_ADMIN_PASSWORD, adminRole
            );

            createUserIfNotExists(
                    "Regular", "User", 25, "user@user.com",
                    DEFAULT_USER_PASSWORD, userRole
            );

            createUserIfNotExists(
                    "Test", "User", 28, "test@test.com",
                    DEFAULT_TEST_PASSWORD, userRole
            );

            logger.info("Data initialization completed successfully");

        } catch (Exception e) {
            logger.error("Error during data initialization", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }

    private void createUserIfNotExists(String firstName, String lastName, int age,
                                       String email, String rawPassword, Role role) {
        if (userDao.existsByEmail(email)) {
            logger.debug("User with email {} already exists", email);
            return;
        }

        try {
            String encodedPassword = passwordService.encode(rawPassword);

            User user = new User(firstName, lastName, age, email, encodedPassword);

            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            userDao.save(user);
            logger.info("Created user: {} with email: {}", user.getFullName(), email);

        } catch (Exception e) {
            logger.error("Failed to create user with email: {}", email, e);
            throw new RuntimeException("Failed to create user: " + email, e);
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        Optional<Role> existing = roleDao.findByName(roleName);
        if (existing.isPresent()) {
            logger.debug("Role {} already exists", roleName);
            return existing.get();
        }

        try {
            Role role = new Role(roleName);
            roleDao.save(role);
            logger.info("Created role: {}", roleName);
            return role;

        } catch (Exception e) {
            logger.error("Failed to create role: {}", roleName, e);
            throw new RuntimeException("Failed to create role: " + roleName, e);
        }
    }
}