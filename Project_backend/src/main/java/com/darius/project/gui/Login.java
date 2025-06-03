package com.darius.project.gui;
import com.darius.project.repository.Database.UserDB;
import com.darius.project.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @Override
    public void start(Stage stage) throws Exception {
        LOGGER.info("Starting application...");
        try {
            UserDB userDB = new UserDB();
            UserService userService = new UserService(userDB);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));

            LoginController loginController = new LoginController(userService);
            fxmlLoader.setController(loginController);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle("Trip Booking System - Login");

            LOGGER.info("Showing main application window");
            stage.show();
        } catch (Exception e) {
            LOGGER.error("Error starting application: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static void main(String[] args) {
        LOGGER.info("Application main method called");
        try { launch(args);
        } catch (Exception e) {
            LOGGER.error("Fatal application error: {}", e.getMessage(), e);
        }
    }
}