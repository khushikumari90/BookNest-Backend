package com.booknest.auth.service;

import com.booknest.auth.entity.User;
import com.booknest.auth.exception.InvalidCredentialsException;
import com.booknest.auth.exception.UserNotFoundException;
import com.booknest.auth.repository.UserRepository;
import com.booknest.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private UserRepository repo;
    @Autowired private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ─── Register Customer ────────────────────────────────────────────────────

    @Override
    public User register(User user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered. Please login.");
        }
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        user.setRole("USER");
        user.setProvider("local");
        user.setCreatedAt(LocalDateTime.now());
        return repo.save(user);
    }

    // ─── Register Admin ───────────────────────────────────────────────────────

    @Override
    public User registerAdmin(User user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        user.setRole("ADMIN");
        user.setProvider("local");
        user.setCreatedAt(LocalDateTime.now());
        return repo.save(user);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Override
    public String login(String email, String password) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No account found with this email."));

        // OAuth users (GitHub) have no password — direct them to GitHub login
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new InvalidCredentialsException(
                    "This account uses GitHub login. Please use the 'Login with GitHub' button.");
        }

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Incorrect password. Please try again.");
        }

        return jwtUtil.generateToken(email);
    }

    // ─── Token ────────────────────────────────────────────────────────────────

    @Override
    public String validateToken(String token) { return jwtUtil.validateToken(token); }

    @Override
    public String refreshToken(String token) {
        String email = jwtUtil.validateToken(token);
        return jwtUtil.generateToken(email);
    }

    @Override
    public void logout(String token) { /* stateless JWT — client discards token */ }

    // ─── Password ─────────────────────────────────────────────────────────────

    @Override
    public void changePassword(String email, String newPassword) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPasswordHash(encoder.encode(newPassword));
        repo.save(user);
    }

    // ─── User CRUD ────────────────────────────────────────────────────────────

    @Override
    public User getUserByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
        if (updatedUser.getMobile()   != null) user.setMobile(updatedUser.getMobile());
        // Only allow role change if explicitly set (admin can promote users)
        if (updatedUser.getRole()     != null) user.setRole(updatedUser.getRole());
        return repo.save(user);
    }

    @Override
    public void deleteUser(Long id) { repo.deleteById(id); }

    @Override
    public List<User> getUsersByRole(String role) { return repo.findAllByRole(role); }
}
