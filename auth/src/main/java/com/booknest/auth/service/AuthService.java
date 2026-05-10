package com.booknest.auth.service;

import com.booknest.auth.entity.User;
import java.util.List;

public interface AuthService {
    User register(User user);
    User registerAdmin(User user);
    String login(String email, String password);
    String validateToken(String token);
    String refreshToken(String token);
    void logout(String token);
    void changePassword(String email, String newPassword);
    User getUserByEmail(String email);
    User getUserById(Long id);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getUsersByRole(String role);
}
