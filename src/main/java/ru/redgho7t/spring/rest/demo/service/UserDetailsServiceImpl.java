package ru.redgho7t.spring.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.redgho7t.spring.rest.demo.model.User;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
        logger.info("UserDetailsService initialized");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting login for email: {}", email);

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("User not found: {}", email);
            throw new UsernameNotFoundException("User with email '" + email + "' not found");
        }

        User user = userOptional.get();
        logger.debug("User found: {} with {} roles", user.getEmail(), user.getRoles().size());

        if (logger.isDebugEnabled()) {
            user.getRoles().forEach(role -> logger.debug("User {} has role: {}", user.getEmail(), role.getName()));
        }

        return user;
    }
}