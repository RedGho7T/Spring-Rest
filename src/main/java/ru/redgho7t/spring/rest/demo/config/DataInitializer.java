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

import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordService passwordService;

    @Autowired
    public DataInitializer(UserDao userDao, RoleDao roleDao, @Lazy PasswordService passwordService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordService = passwordService;
        logger.info("DataInitializer initialized with PasswordService");
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting data initialization");

        if (!passwordService.isPasswordCacheInitialized()) {
            logger.error("Password cache is not ready");
            throw new IllegalStateException("Password cache not initialized");
        }
        logger.info("Password cache ready: {} entries", passwordService.getCachedPasswordCount());


        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");


        if (!userDao.existsByEmail("admin@admin.com")) {
            String adminPass = passwordService.getEncodedAdminPassword();
            User admin = new User("Администратор", 30, "admin@admin.com", adminPass);
            admin.setRoles(Set.of(adminRole));
            userDao.save(admin);
            logger.info("Created admin with cached password");
        }


        if (!userDao.existsByEmail("user@user.com")) {
            String userPass = passwordService.getEncodedUserPassword();
            User user = new User("Обычный пользователь", 25, "user@user.com", userPass);
            user.setRoles(Set.of(userRole));
            userDao.save(user);
            logger.info("Created user with cached password");
        }


        if (!userDao.existsByEmail("test@test.com")) {
            String testPass = passwordService.getEncodedTestPassword();
            User test = new User("Тестовый пользователь", 28, "test@test.com", testPass);
            test.setRoles(Set.of(userRole));
            userDao.save(test);
            logger.info("Created test user with cached password");
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        Optional<Role> existing = roleDao.findByName(roleName);
        if (existing.isPresent()) {
            logger.debug("Role {} already exists", roleName);
            return existing.get();
        }

        Role role = new Role(roleName);
        roleDao.save(role);
        logger.info("Created role: {}", roleName);
        return role;
    }
}
