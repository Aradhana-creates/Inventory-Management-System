package com.pms.ui;

import com.ims.common.util.AlertUtil;
import com.pms.dao.UserDAO;
import com.pms.model.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controller for Login view. Authenticates credentials against UserDAO and verifies BCrypt hash.
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button loginButton;

    private final UserDAO userDAO = new UserDAO();

    /**
     * Optional callback invoked when login succeeds (e.g. for scene navigation).
     */
    private Consumer<User> onLoginSuccessCallback;

    public void setOnLoginSuccessCallback(Consumer<User> callback) {
        this.onLoginSuccessCallback = callback;
    }

    @FXML
    public void initialize() {
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    /**
     * Handles the login action triggered by the user.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField != null ? usernameField.getText().trim() : "";
        String password = passwordField != null ? passwordField.getText() : "";

        if (username.isEmpty() || password.isEmpty()) {
            setMessage("Please enter both username and password.", true);
            return;
        }

        // Disable button while processing
        setFormBusy(true);
        setMessage("Authenticating...", false);

        Task<Optional<User>> authTask = new Task<>() {
            @Override
            protected Optional<User> call() {
                try {
                    Optional<User> userOpt = userDAO.findByUsername(username);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        if (user.isActive() && BCrypt.checkpw(password, user.getPasswordHash())) {
                            return Optional.of(user);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Authentication error for user: {}", username, e);
                }
                return Optional.empty();
            }
        };

        authTask.setOnSucceeded(e -> {
            setFormBusy(false);
            Optional<User> result = authTask.getValue();
            if (result.isPresent()) {
                User user = result.get();
                UserSession.getInstance().setCurrentUser(user);
                setMessage("Login Successful", false);
                logger.info("User logged in successfully: {} [{}]", user.getUsername(), user.getRole());

                if (onLoginSuccessCallback != null) {
                    onLoginSuccessCallback.accept(user);
                }
            } else {
                setMessage("Invalid Credentials", true);
            }
        });

        authTask.setOnFailed(e -> {
            setFormBusy(false);
            setMessage("Authentication failed. Please check database connection.", true);
            AlertUtil.showError("Login Error", "Unable to connect to the authentication service.");
        });

        new Thread(authTask).start();
    }

    /**
     * Clears username and password fields.
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        if (usernameField != null) usernameField.clear();
        if (passwordField != null) passwordField.clear();
        if (messageLabel != null) messageLabel.setText("");
    }

    private void setFormBusy(boolean busy) {
        if (loginButton != null) {
            loginButton.setDisable(busy);
        }
        if (usernameField != null) {
            usernameField.setDisable(busy);
        }
        if (passwordField != null) {
            passwordField.setDisable(busy);
        }
    }

    private void setMessage(String message, boolean isError) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            if (isError) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else {
                messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
        }
    }
}
