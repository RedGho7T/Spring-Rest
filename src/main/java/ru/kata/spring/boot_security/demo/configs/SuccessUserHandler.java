package ru.kata.spring.boot_security.demo.configs;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse resp,
                                        Authentication auth) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        System.out.println("🔍 DEBUG: Пользователь вошёл с ролями: " + roles);

        try {
            if (roles.contains("ROLE_ADMIN")) {
                System.out.println("✅ Перенаправляем ADMIN на /admin");
                resp.sendRedirect("/admin");
            } else if (roles.contains("ROLE_USER")) {
                System.out.println("✅ Перенаправляем USER на /user");
                resp.sendRedirect("/user");
            } else {
                System.out.println("⚠️ У пользователя нет ролей, направляем на главную");
                resp.sendRedirect("/");
            }
        } catch (Exception e) {
            System.err.println("❌ ОШИБКА в SuccessUserHandler: " + e.getMessage());
            e.printStackTrace();
            resp.sendRedirect("/");
        }
    }
}