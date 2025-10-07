package ru.redgho7t.spring.rest.demo.service;

public interface PasswordService {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

    boolean isValidPassword(String password);

    String generateTemporaryPassword();
}
