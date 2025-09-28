package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Главная страница
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html
    }

    // Страница входа
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}