package ru.redgho7t.spring.rest.demo.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.redgho7t.spring.rest.demo.dto.UpdateUserRequest;
import ru.redgho7t.spring.rest.demo.dto.UserResponse;
import ru.redgho7t.spring.rest.demo.model.Role;
import ru.redgho7t.spring.rest.demo.model.User;
import ru.redgho7t.spring.rest.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
        logger.info("UserRestController initialized");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        logger.info("REST: Getting current user profile for: {}", user.getEmail());
        try {
            UserResponse userResponse = new UserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            logger.error("Error getting current user profile", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody UpdateUserRequest request) {
        logger.info("REST: Updating profile for user: {}", user.getEmail());
        try {
            Set<Long> roleIds = user.getRoles().stream()
                    .map(role -> role.getId())
                    .collect(Collectors.toSet());

            User updatedUser = userService.updateUser(user.getId(),
                    request.getFirstName(), request.getLastName(), request.getAge(),
                    request.getEmail(), request.getPassword(), roleIds);

            return ResponseEntity.ok(new UserResponse(updatedUser));
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal User user) {
        logger.info("REST: Getting all users for user: {}", user.getEmail());
        try {
            boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

            if (isAdmin) {
                List<UserResponse> responses = userService.getAllUsers().stream()
                        .map(UserResponse::new)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(responses);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            logger.error("Error getting all users", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getCurrentUserRoles(@AuthenticationPrincipal User user) {
        logger.info("REST: Getting roles for current user: {}", user.getEmail());
        try {
            List<Role> rolesList = user.getRoles().stream().collect(Collectors.toList());
            return ResponseEntity.ok(rolesList);
        } catch (Exception e) {
            logger.error("Error getting current user roles", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}