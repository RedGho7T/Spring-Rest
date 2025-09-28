package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Попытка входа с email: " + email);

        try {
            User user = userService.getByEmail(email);
            if (user == null) {
                System.out.println("❌ Пользователь НЕ НАЙДЕН: " + email);
                throw new UsernameNotFoundException("Пользователь с email '" + email + "' не найден");
            }

            System.out.println("✅ Найден пользователь: " + user.getName());
            System.out.println("✅ Роли: " + user.getRoles().size());

            // Выводим роли для отладки
            user.getRoles().forEach(role -> {
                System.out.println("  - Роль: " + role.getName());
            });

            return user; // Возвращаем пользователя как UserDetails

        } catch (Exception e) {
            System.out.println("❌ ОШИБКА при поиске пользователя: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("Ошибка при поиске пользователя: " + email, e);
        }
    }

}