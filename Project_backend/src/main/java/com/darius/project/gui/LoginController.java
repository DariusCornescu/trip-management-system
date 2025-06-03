package com.darius.project.gui;
import com.darius.project.networking.Client;
import com.darius.project.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    public LoginController(UserService userService) { this.userService = userService; }
    @FXML private TextField UsernameField;
    @FXML private TextField PasswordField;
    @FXML private Button ConnectButton;
    private final UserService userService;

    @FXML
    public void initialize() {
        LOGGER.info("Initializing LoginController UI components");
        LOGGER.info("Initializing connection to server");
        try {
            Client.getInstance();
            LOGGER.info("Successfully connected to server");
        } catch (Exception e) {
            LOGGER.error("Failed to connect to server: {}", e.getMessage(), e);
        }

        ConnectButton.setOnAction(e -> handleLogin());

        Client.getInstance().setOnMessageReceivedListener(message -> {
            LOGGER.debug("Received message from server: {}", message);

            if (message.equals("LOGIN_SUCCESS")) {
                LOGGER.info("Login successful for user: {}", UsernameField.getText());
                Platform.runLater(() -> {
                    if (UsernameField.getText().equals("Admin") && PasswordField.getText().equals("4862")) {
                        LOGGER.info("Loading admin interface for user: {}", UsernameField.getText());
                        loadOwnerRightsUI();
                    } else {
                        LOGGER.info("Loading standard user interface for user: {}", UsernameField.getText());
                        loadUserInterfaceUI();
                    }
                });
            } else if (message.equals("LOGIN_FAILED")) {
                LOGGER.warn("Login failed for user: {}", UsernameField.getText());
                Platform.runLater(() -> showError("Invalid username/password."));
            } else {
                LOGGER.debug("Unhandled message: {}", message);
            }
        });
    }

    private void showError(String msg) {
        LOGGER.warn("Showing error dialog: {}", msg);
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private void handleLogin() {
        String username = UsernameField.getText();
        String password = PasswordField.getText();
        LOGGER.info("Login attempt for username: {}", username);

        if (username.isEmpty() || password.isEmpty()) {
            LOGGER.warn("Login fields empty - username: '{}', password empty: {}", username, password.isEmpty());
            showError("Please fill in both fields");
            return;
        }

        LOGGER.debug("Performing local authentication check");
        if (userService.checkLogin(username, password)) {
            LOGGER.info("Local authentication passed for user: {}, sending to server", username);
            Client.getInstance().sendMessage("LOGIN#" + username + "#" + password);
        } else {
            LOGGER.warn("Local authentication failed for user: {}", username);
            showError("Invalid username/password.");
        }
    }

    private void loadOwnerRightsUI() {
        LOGGER.info("Loading Owner Rights UI");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OwnerRights.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ConnectButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Owner Rights");
            LOGGER.info("Displaying Owner Rights UI");
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error loading owner interface: {}", e.getMessage(), e);
            showError("Error loading owner interface: " + e.getMessage());
        }
    }

    private void loadUserInterfaceUI() {
        LOGGER.info("Loading User Interface UI");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ConnectButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Interface");
            LOGGER.info("Displaying User Interface UI");
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error loading user interface: {}", e.getMessage(), e);
            showError("Error loading user interface: " + e.getMessage());
        }
    }
}