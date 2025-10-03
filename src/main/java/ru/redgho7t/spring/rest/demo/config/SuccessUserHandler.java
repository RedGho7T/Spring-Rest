package ru.redgho7t.spring.rest.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SuccessUserHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse resp,
                                        Authentication auth) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        logger.info("User logged in with roles: {}", roles);

        try {
            if (roles.contains("ROLE_ADMIN")) {
                logger.info("Redirecting ADMIN to /admin");
                resp.sendRedirect("/admin");
            } else if (roles.contains("ROLE_USER")) {
                logger.info("Redirecting USER to /user");
                resp.sendRedirect("/user");
            } else {
                logger.warn("User has no roles, redirecting to index");
                resp.sendRedirect("/");
            }
        } catch (Exception e) {
            logger.error("Error in SuccessUserHandler: {}", e.getMessage(), e);
            resp.sendRedirect("/");
        }
    }
}
