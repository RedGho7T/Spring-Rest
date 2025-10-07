package ru.redgho7t.spring.rest.demo.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.redgho7t.spring.rest.demo.dto.*;
import ru.redgho7t.spring.rest.demo.model.User;
import ru.redgho7t.spring.rest.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthRestController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        logger.info("AuthRestController initialized");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        logger.info("REST: Login attempt for email: {}", request.getEmail());

        try {
            Optional<User> userOpt = userService.getUserByEmail(request.getEmail());
            if (!userOpt.isPresent()) {
                logger.warn("User not found: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = userOpt.get();
            UserResponse userResponse = new UserResponse(user);

            logger.info("User {} successfully logged in", user.getEmail());
            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            logger.error("Login error for user: " + request.getEmail(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            try {
                Optional<User> userOpt = userService.getUserByEmail(auth.getName());
                if (userOpt.isPresent()) {
                    return ResponseEntity.ok(new UserResponse(userOpt.get()));
                }
            } catch (Exception e) {
                logger.error("Error checking auth status", e);
            }
        }

        return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {
        logger.info("REST: Registration attempt for email: {}", request.getEmail());

        try {
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email already exists"));
            }

            User user = userService.registerUser(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getAge(),
                    request.getEmail(),
                    request.getPassword()
            );

            logger.info("User {} successfully registered", user.getEmail());
            return ResponseEntity.ok(new UserResponse(user));

        } catch (Exception e) {
            logger.error("Registration error for email: " + request.getEmail(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Registration failed"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        logger.info("REST: Checking email availability: {}", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(new EmailCheckResponse(!exists, exists ?
                "Email is already taken" : "Email is available"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("REST: Logout request");

        try {
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return ResponseEntity.ok(new ErrorResponse("Logged out successfully"));

        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Logout failed"));
        }
    }

    public static class EmailCheckResponse {
        private boolean available;
        private String message;

        public EmailCheckResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}