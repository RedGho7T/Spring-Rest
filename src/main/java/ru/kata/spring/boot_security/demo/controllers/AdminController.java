package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Список пользователей
    @GetMapping
    public String listUsers(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            System.out.println("✅ Загружено пользователей: " + users.size());
            return "admin/list";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке пользователей: " + e.getMessage());
            model.addAttribute("error", "Ошибка при загрузке списка пользователей");
            return "admin/list";
        }
    }

    // Форма создания пользователя
    @GetMapping("/new")
    public String newUserForm(Model model) {
        try {
            model.addAttribute("user", new User());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/new";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке формы создания: " + e.getMessage());
            model.addAttribute("error", "Ошибка при загрузке формы");
            return "admin/list";
        }
    }

    // ОБНОВЛЕННОЕ создание пользователя с поддержкой firstName/lastName
    @PostMapping
    public String createUser(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("age") int age,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam(value = "roles", required = false) String[] roleIds,
                             Model model) {
        try {
            System.out.println("🔄 Создаём пользователя: " + firstName + " " + lastName);

            // Создаем нового пользователя с новыми полями
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setEmail(email);
            user.setPassword(password);

            // Обрабатываем роли
            Set<Role> roles = new HashSet<>();
            if (roleIds != null && roleIds.length > 0) {
                for (String roleIdStr : roleIds) {
                    try {
                        Long roleId = Long.parseLong(roleIdStr);
                        Role role = roleService.getAllRoles().stream()
                                .filter(r -> r.getId().equals(roleId))
                                .findFirst()
                                .orElse(null);
                        if (role != null) {
                            roles.add(role);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Неверный ID роли: " + roleIdStr);
                    }
                }
            } else {
                // По умолчанию назначаем роль USER
                Role userRole = roleService.getRoleByName("ROLE_USER");
                if (userRole != null) {
                    roles.add(userRole);
                }
            }

            user.setRoles(roles);
            userService.saveUser(user);

            System.out.println("✅ Пользователь создан успешно: " + user.getFullName());
            return "redirect:/admin";

        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании пользователя: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/new";
        }
    }

    // Форма редактирования пользователя
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                System.out.println("❌ Пользователь с ID " + id + " не найден");
                return "redirect:/admin";
            }

            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке формы редактирования: " + e.getMessage());
            return "redirect:/admin";
        }
    }

    // ОБНОВЛЕННЫЙ метод обновления пользователя с поддержкой firstName/lastName
    @PostMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("age") int age,
                             @RequestParam("email") String email,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "roleIds", required = false) String[] roleIds,
                             Model model) {
        try {
            System.out.println("🔄 Обновляем пользователя с ID: " + id);

            // Получаем существующего пользователя
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                System.out.println("❌ Пользователь с ID " + id + " не найден");
                return "redirect:/admin";
            }

            // Обновляем поля
            existingUser.setId(id);
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setAge(age);
            existingUser.setEmail(email);

            // Обрабатываем роли
            Set<Role> roles = new HashSet<>();
            if (roleIds != null && roleIds.length > 0) {
                for (String roleIdStr : roleIds) {
                    try {
                        Long roleId = Long.parseLong(roleIdStr);
                        Role role = roleService.getAllRoles().stream()
                                .filter(r -> r.getId().equals(roleId))
                                .findFirst()
                                .orElse(null);
                        if (role != null) {
                            roles.add(role);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Неверный ID роли: " + roleIdStr);
                    }
                }
                existingUser.setRoles(roles);
                System.out.println("✅ Установлены роли: " + roles.size());
            } else {
                // Если роли не выбраны, оставляем старые
                System.out.println("⚠️ Роли не изменились");
            }

            // Обрабатываем пароль
            if (password != null && !password.trim().isEmpty()) {
                existingUser.setPassword(password);
                System.out.println("✅ Пароль обновлен");
            } else {
                System.out.println("⚠️ Пароль не изменился");
            }

            // Сохраняем пользователя
            userService.updateUser(existingUser);
            System.out.println("✅ Пользователь обновлен: " + existingUser.getFullName());
            return "redirect:/admin";

        } catch (Exception e) {
            System.out.println("❌ Ошибка при обновлении пользователя: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        }
    }

    // Удаление пользователя
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            System.out.println("🗑️ Удаляем пользователя с ID: " + id);
            userService.deleteUser(id);
            System.out.println("✅ Пользователь удален успешно");
            return "redirect:/admin";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при удалении пользователя: " + e.getMessage());
            return "redirect:/admin";
        }
    }
}