package com.darius.project.service;

import com.darius.project.domain.User;
import com.darius.project.repository.GenericRepos.UserRepository;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User findByUsername(String username) {
        LOGGER.info("Service: Finding user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            LOGGER.info("Service: Found user with username: {}, ID: {}", username, user.getId());
        } else {
            LOGGER.info("Service: No user found with username: {}", username);
        }
        return user;
    }

    public boolean checkLogin(String username, String password) {
        LOGGER.info("Service: Checking login credentials for username: {}", username);

        User user = findByUsername(username);
        if (user == null) {
            LOGGER.info("Service: Login failed - user not found with username: {}", username);
            return false;
        }

        boolean loginSuccess = user.getPassword().equals(password);
        if (loginSuccess) {
            LOGGER.info("Service: Login successful for user: {}", username);
        } else {
            LOGGER.info("Service: Login failed - invalid password for user: {}", username);
        }

        return loginSuccess;
    }

    public User findById(Integer id) {
        LOGGER.info("Service: Finding user by ID: {}", id);

        User user = userRepository.findById(id);
        if (user != null) {
            LOGGER.info("Service: Found user with ID: {}, username: {}", id, user.getUsername());
        } else {
            LOGGER.info("Service: No user found with ID: {}", id);
        }
        return user;
    }

    public void save(Integer id, User user) {
        if (user == null) {
            LOGGER.error("Service: Cannot save null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        LOGGER.info("Service: Saving user with ID: {}, username: {}", id, user.getUsername());

        try {
            User existingUser = findByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                LOGGER.error("Service: Cannot save user - username '{}' already exists with ID: {}",
                        user.getUsername(), existingUser.getId());
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }

            userRepository.save(id, user);
            LOGGER.info("Service: Successfully saved user with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error saving user with ID: {}, username: {}, error: {}",
                    id, user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    public void update(Integer id, User user) {
        if (user == null) {
            LOGGER.error("Service: Cannot update null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        LOGGER.info("Service: Updating user with ID: {}, username: {}", id, user.getUsername());

        try {
            User existingUser = findById(id);
            if (existingUser == null) {
                LOGGER.error("Service: Cannot update - user with ID {} not found", id);
                throw new IllegalArgumentException("User not found with ID: " + id);
            }

            if (!existingUser.getUsername().equals(user.getUsername())) {
                User userWithSameUsername = findByUsername(user.getUsername());
                if (userWithSameUsername != null && !userWithSameUsername.getId().equals(id)) {
                    LOGGER.error("Service: Cannot update user - username '{}' already exists with ID: {}",
                            user.getUsername(), userWithSameUsername.getId());
                    throw new IllegalArgumentException("Username already exists: " + user.getUsername());
                }
                LOGGER.info("Service: User ID: {} changing username from '{}' to '{}'",
                        id, existingUser.getUsername(), user.getUsername());
            }

            userRepository.update(id, user);
            LOGGER.info("Service: Successfully updated user with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error updating user with ID: {}, username: {}, error: {}",
                    id, user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        LOGGER.info("Service: Deleting user with ID: {}", id);

        try {
            User existingUser = findById(id);
            if (existingUser == null) {
                LOGGER.warn("Service: Attempting to delete non-existent user with ID: {}", id);
            } else {
                LOGGER.info("Service: Deleting user with username: {}", existingUser.getUsername());
            }

            userRepository.delete(id);
            LOGGER.info("Service: Successfully deleted user with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error deleting user with ID: {}, error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public List<User> findAll() {
        LOGGER.info("Service: Retrieving all users");
        List<User> users = new ArrayList<>();
        Iterator<User> userIterator = userRepository.findAll();

        int count = 0;
        while (userIterator.hasNext()) {
            users.add(userIterator.next());
            count++;
        }

        LOGGER.info("Service: Retrieved {} users total", count);
        return users;
    }
}