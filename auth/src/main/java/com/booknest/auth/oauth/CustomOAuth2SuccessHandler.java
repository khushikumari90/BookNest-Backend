package com.booknest.auth.oauth;

import com.booknest.auth.entity.User;
import com.booknest.auth.repository.UserRepository;
import com.booknest.auth.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private UserRepository repo;
    @Autowired private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String tempEmail = oauthUser.getAttribute("email");
        String name      = oauthUser.getAttribute("name");
        String login     = oauthUser.getAttribute("login");

        // GitHub often hides email — use login-based fallback
        if (tempEmail == null || tempEmail.isBlank()) {
            tempEmail = login + "@github.com";
        }
        final String email = tempEmail;
        final String displayName = (name != null && !name.isBlank()) ? name : login;

        // Find or create user in DB
        User user = repo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(displayName);
            newUser.setRole("USER");
            newUser.setProvider("github");
            newUser.setCreatedAt(LocalDateTime.now());
            return repo.save(newUser);
        });

        String token = jwtUtil.generateToken(user.getEmail());

        // Redirect to booknest-web OAuth callback endpoint with token + user info in params
        String callbackUrl = "http://localhost:4200/auth/oauth-callback"
                + "?token="    + URLEncoder.encode(token,             StandardCharsets.UTF_8)
                + "&email="    + URLEncoder.encode(user.getEmail(),    StandardCharsets.UTF_8)
                + "&name="     + URLEncoder.encode(user.getFullName(), StandardCharsets.UTF_8)
                + "&userId="   + user.getUserId()
                + "&role="     + URLEncoder.encode(user.getRole(),     StandardCharsets.UTF_8);

        response.sendRedirect(callbackUrl);
    }
}
