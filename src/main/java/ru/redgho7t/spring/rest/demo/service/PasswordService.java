package ru.redgho7t.spring.rest.demo.service;

public interface PasswordService {

    String encodePassword(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

    String getEncodedAdminPassword();

    String getEncodedUserPassword();

    String getEncodedTestPassword();

    boolean isPasswordCacheInitialized();

    int getCachedPasswordCount();
}
