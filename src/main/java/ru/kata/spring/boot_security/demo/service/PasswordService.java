package ru.kata.spring.boot_security.demo.service;

public interface PasswordService {

    String encodePassword(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

    String getEncodedAdminPassword();

    String getEncodedUserPassword();

    String getEncodedTestPassword();

    boolean isPasswordCacheInitialized();

    int getCachedPasswordCount();
}
