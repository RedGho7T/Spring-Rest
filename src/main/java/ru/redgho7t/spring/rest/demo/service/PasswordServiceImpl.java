package ru.redgho7t.spring.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordServiceImpl implements PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> passwordCache = new ConcurrentHashMap<>();

    private static final String DEFAULT_ADMIN_PASSWORD = "admin";
    private static final String DEFAULT_USER_PASSWORD = "user";
    private static final String DEFAULT_TEST_PASSWORD = "test";

    private static final String CACHE_KEY_ADMIN = "default_admin";
    private static final String CACHE_KEY_USER = "default_user";
    private static final String CACHE_KEY_TEST = "default_test";

    private volatile boolean cacheInitialized = false;

    @Autowired
    public PasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        logger.debug("PasswordService initialized without circular dependencies");
    }

    @PostConstruct
    public void initializePasswordCache() {
        logger.info("Starting password cache initialization...");

        try {
            Long startTime = System.currentTimeMillis();

            String encodedAdmin = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);
            String encodedUser = passwordEncoder.encode(DEFAULT_USER_PASSWORD);
            String encodedTest = passwordEncoder.encode(DEFAULT_TEST_PASSWORD);

            passwordCache.put(CACHE_KEY_ADMIN, encodedAdmin);
            passwordCache.put(CACHE_KEY_USER, encodedUser);
            passwordCache.put(CACHE_KEY_TEST, encodedTest);

            long endTime = System.currentTimeMillis();
            cacheInitialized = true;

            logger.info("Password cache initialized successfully!");
            logger.info("Pre-encoded {} passwords in {} ms",
                    passwordCache.size(), (endTime - startTime));
            logger.debug("Cached keys: {}", passwordCache.keySet());
        } catch (Exception e) {
            logger.error("Critical error initializing password cache: {}", e.getMessage(), e);
            cacheInitialized = false;
            throw new IllegalStateException("Failed to initialize password cache", e);
        }
    }

    @Override
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        logger.debug("Encoding new password (length: {} characters)", rawPassword.length());

        try {
            String encoded = passwordEncoder.encode(rawPassword);
            logger.debug("Password encoded successfully");
            return encoded;
        } catch (Exception e) {
            logger.error("Error encoding password: {}", e.getMessage());
            throw new RuntimeException("Password encoding failed", e);
        }
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            logger.debug("Password check: one of the passwords is null");
            return false;
        }
        try {
            boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
            logger.debug("Password verification result: {}", matches ? "matches" : "does not match");
            return matches;
        } catch (Exception e) {
            logger.error("Password check failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getEncodedAdminPassword() {
        return getCachedPassword(CACHE_KEY_ADMIN, "admin");
    }

    @Override
    public String getEncodedUserPassword() {
        return getCachedPassword(CACHE_KEY_USER, "user");
    }

    @Override
    public String getEncodedTestPassword() {
        return getCachedPassword(CACHE_KEY_TEST, "test");
    }

    private String getCachedPassword(String cacheKey, String passwordType) {
        if (!cacheInitialized) {
            logger.error("Password cache not initialized! Called too early.");
            throw new IllegalStateException("Password cache not ready!");
        }
        String cachedPassword = passwordCache.get(cacheKey);

        if (cachedPassword == null) {
            logger.error("Cached password '{}' not found!", passwordType);
            throw new IllegalStateException("Cached password not found: " + passwordType);
        }
        logger.debug("Retrieved cached password: {}", passwordType);
        return cachedPassword;
    }

    @Override
    public boolean isPasswordCacheInitialized() {
        return cacheInitialized;
    }

    @Override
    public int getCachedPasswordCount() {
        return passwordCache.size();
    }

}
