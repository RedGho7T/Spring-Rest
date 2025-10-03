package ru.kata.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        logger.info("AdminController initialized");
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/list";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping
    public String createUser(@ModelAttribute User user,
                             @RequestParam(value = "roles", required = false) Long[] roleIds) {
        logger.info("Received request to create user: {}", user.getEmail());
        Set<Long> roles = roleIds != null
                ? Stream.of(roleIds).collect(Collectors.toSet())
                : Set.of();
        userService.createUser(user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getPassword(),
                roles);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> u = userService.getUserById(id);
        if (u.isEmpty()) {
            return "redirect:/admin";
        }
        model.addAttribute("user", u.get());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User user,
                             @RequestParam(value = "roles", required = false) Long[] roleIds) {
        logger.info("Received request to update user ID: {}", id);
        Set<Long> roles = roleIds != null
                ? Stream.of(roleIds).collect(Collectors.toSet())
                : Set.of();
        userService.updateUser(id,
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getPassword(),
                roles);
        return "redirect:/admin";
    }


    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        logger.info("Received request to delete user ID: {}", id);
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}