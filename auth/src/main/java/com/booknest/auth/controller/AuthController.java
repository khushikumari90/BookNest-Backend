package com.booknest.auth.controller;

import com.booknest.auth.dto.LoginRequest;
import com.booknest.auth.dto.RegisterRequest;
import com.booknest.auth.entity.User;
import com.booknest.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication & Users", description = "Register, login, token management and user profile operations")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ─── Register ────────────────────────────────────────────────────────────

    @Operation(summary = "Register a new customer",
               description = "Creates a new customer account. Role is set to CUSTOMER automatically.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer registered successfully",
                     content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Email already exists or invalid data", content = @Content)
    })
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req) {
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(req.getPassword());
        user.setMobile(req.getMobile());
        return authService.register(user);
    }

    @Operation(summary = "Register a new admin",
               description = "Creates an administrator account. Requires admin secret key validation on the frontend.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Admin registered successfully",
                     content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
    })
    @PostMapping("/register/admin")
    public User registerAdmin(@RequestBody RegisterRequest req) {
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(req.getPassword());
        user.setMobile(req.getMobile());
        return authService.registerAdmin(user);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Operation(summary = "Login with email and password",
               description = "Authenticates the user and returns a JWT Bearer token. Include this token in the Authorization header for all protected endpoints.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "JWT token returned as plain string"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        return authService.login(req.getEmail(), req.getPassword());
    }

    // ─── Profile ──────────────────────────────────────────────────────────────

    @Operation(summary = "Get user profile by email",
               description = "Fetches the full user object for the given email. Called by the frontend after login to populate the session.")
    @ApiResponse(responseCode = "200", description = "User profile returned",
                 content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping("/profile")
    public User profile(
            @Parameter(description = "Email address of the user", example = "john@example.com")
            @RequestParam String email) {
        return authService.getUserByEmail(email);
    }

    @Operation(summary = "Refresh JWT token", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "New JWT token returned")
    @PostMapping("/refresh")
    public String refresh(@RequestHeader("Authorization") String token) {
        return authService.refreshToken(token.substring(7));
    }

    @Operation(summary = "Change user password", security = @SecurityRequirement(name = "BearerAuth"),
               description = "Body: { \"email\": \"...\", \"newPassword\": \"...\" }")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @PostMapping("/change-password")
    public String changePassword(@RequestBody Map<String, String> req) {
        authService.changePassword(req.get("email"), req.get("newPassword"));
        return "Password changed successfully";
    }

    @Operation(summary = "Logout", description = "Stateless logout — client should discard the JWT token.")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }

    // ─── User Management ──────────────────────────────────────────────────────

    @Operation(summary = "Get user by ID", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/user/{id}")
    public User getUserById(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        return authService.getUserById(id);
    }

    @Operation(summary = "Update user profile", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "User updated",
                 content = @Content(schema = @Schema(implementation = User.class)))
    @PutMapping("/user/{id}")
    public User updateUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id,
            @RequestBody User updatedUser) {
        return authService.updateUser(id, updatedUser);
    }

    @Operation(summary = "Delete user account", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "User deleted")
    @DeleteMapping("/user/{id}")
    public String deleteUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        authService.deleteUser(id);
        return "User deleted successfully";
    }

    @Operation(summary = "Get all users by role", security = @SecurityRequirement(name = "BearerAuth"),
               description = "Admin endpoint. role = CUSTOMER or ADMIN")
    @ApiResponse(responseCode = "200", description = "List of users with given role")
    @GetMapping("/users/role/{role}")
    public List<User> getUsersByRole(
            @Parameter(description = "Role: CUSTOMER or ADMIN", example = "CUSTOMER")
            @PathVariable String role) {
        return authService.getUsersByRole(role);
    }
}
