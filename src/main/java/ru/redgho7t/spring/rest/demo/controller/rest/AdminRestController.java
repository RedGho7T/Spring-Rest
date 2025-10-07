package ru.redgho7t.spring.rest.demo.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.redgho7t.spring.rest.demo.dto.*;
import ru.redgho7t.spring.rest.demo.model.User;
import ru.redgho7t.spring.rest.demo.service.RoleService;
import ru.redgho7t.spring.rest.demo.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRestController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        logger.info("AdminRestController initialized");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        logger.info("REST: Getting all users");
        try {
            List<UserResponse> responses = userService.getAllUsers().stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting all users", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to fetch users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("REST: Getting user by ID: {}", id);
        try {
            Optional<User> opt = userService.getUserById(id);
            if (opt.isPresent()) {
                UserResponse resp = new UserResponse(opt.get());
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.status(404)
                        .body(new ErrorResponse("User not found with ID: " + id));
            }
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to get user: " + e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        logger.info("REST: Creating user with email: {}", req.getEmail());
        try {

            if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email is required"));
            }

            if (userService.existsByEmail(req.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email already exists: " + req.getEmail()));
            }

            User user = userService.createUser(
                    req.getFirstName(), req.getLastName(), req.getAge(),
                    req.getEmail(), req.getPassword(), req.getRoleIds()
            );

            logger.info("Successfully created user: {}", user.getEmail());
            return ResponseEntity.ok(new UserResponse(user));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating user", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        logger.info("REST: Updating user ID: {}", id);
        try {

            if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email is required"));
            }

            if (userService.getUserById(id).isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ErrorResponse("User not found with ID: " + id));
            }

            User user = userService.updateUser(
                    id, req.getFirstName(), req.getLastName(),
                    req.getAge(), req.getEmail(), req.getPassword(), req.getRoleIds()
            );

            logger.info("Successfully updated user: {}", user.getEmail());
            return ResponseEntity.ok(new UserResponse(user));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating user", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user ID: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("REST: Deleting user ID: {}", id);
        try {

            if (userService.getUserById(id).isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ErrorResponse("User not found with ID: " + id));
            }

            userService.deleteUser(id);
            logger.info("Successfully deleted user ID: {}", id);
            return ResponseEntity.ok(new ErrorResponse("User deleted successfully"));

        } catch (Exception e) {
            logger.error("Error deleting user ID: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to delete user: " + e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        logger.info("REST: Getting all roles");
        try {
            return ResponseEntity.ok(roleService.getAllRoles());
        } catch (Exception e) {
            logger.error("Error getting all roles", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to fetch roles: " + e.getMessage()));
        }
    }
}