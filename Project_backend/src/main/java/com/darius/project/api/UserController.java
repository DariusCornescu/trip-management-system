package com.darius.project.api;

import com.darius.project.domain.User;
import com.darius.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("Login attempt for username: {}", request.getUsername());

        try {
            boolean isValid = userService.checkLogin(request.getUsername(), request.getPassword());
            if (isValid) {
                User user = userService.findByUsername(request.getUsername());
                boolean isAdmin = "Admin".equals(user.getUsername()) && "4862".equals(user.getPassword());

                LoginResponse response = new LoginResponse(
                        true,
                        "Login successful",
                        user.getId(),
                        user.getUsername(),
                        isAdmin
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Invalid credentials", null, null, false));
            }
        } catch (Exception e) {
            LOGGER.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(false, "Login failed", null, null, false));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = new User(request.getId(), request.getUsername(), request.getPassword());
            userService.save(request.getId(), user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CreateUserResponse(true, "User created successfully", user));
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CreateUserResponse(false, "Error creating user: " + e.getMessage(), null));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        try {
            User user = new User(id, request.getUsername(), request.getPassword());
            userService.update(id, user);
            return ResponseEntity.ok(new UpdateUserResponse(true, "User updated successfully", user));
        } catch (Exception e) {
            LOGGER.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UpdateUserResponse(false, "Error updating user: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            LOGGER.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting user"));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String username) {
        LOGGER.info("Searching users with username containing: {}", username);
        try {
            List<User> users = userService.findAll(); // For now, return all users filtered by username
            // You can implement a proper search method in UserService if needed
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("Error searching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private boolean success;
        private String message;
        private Integer userId;
        private String username;
        private boolean isAdmin;

        public LoginResponse(boolean success, String message, Integer userId, String username, boolean isAdmin) {
            this.success = success;
            this.message = message;
            this.userId = userId;
            this.username = username;
            this.isAdmin = isAdmin;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public boolean isAdmin() { return isAdmin; }
        public void setAdmin(boolean admin) { isAdmin = admin; }
    }

    public static class CreateUserRequest {
        private Integer id;
        private String username;
        private String password;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class CreateUserResponse {
        private boolean success;
        private String message;
        private User user;

        public CreateUserResponse(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    public static class UpdateUserRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateUserResponse {
        private boolean success;
        private String message;
        private User user;

        public UpdateUserResponse(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}