package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Главная страница теперь перенаправляет на login
    @GetMapping("/")
    public String index() {
        return "login";
    }

    // Страница входа (дублируем для явного доступа)
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}