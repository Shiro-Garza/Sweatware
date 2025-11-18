package edu.utsa.cs3443.sweatware_alpha;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javax.swing.*;

public class UIController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField textField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmTextField;
    @FXML
    private CheckBox confirmPasswordToggle;
    @FXML
    private Label successLabel;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Label errorLabel;

    @FXML
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
        }
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        if (confirmPasswordToggle.isSelected()) {
            confirmTextField.setText(confirmPasswordField.getText());
            confirmTextField.setVisible(true);
            confirmTextField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            confirmPasswordField.setText(confirmTextField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmTextField.setVisible(false);
            confirmTextField.setManaged(false);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = getPasswordText();

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        // Check credentials against CSV
        File file = new File("data/users.csv");
        if (!file.exists()) {
            showError("User database not found.");
            return;
        }

        boolean authenticated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    // Format: ID,Username,Password
                    String storedUsername = parts[1].trim();
                    String storedPassword = parts[2].trim();

                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        authenticated = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            showError("Error reading user data.");
            return;
        }

        if (authenticated) {
            // Login successful - navigate to dashboard
            try {
                Parent root = FXMLLoader.load(getClass().getResource("dashboard-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 440, 956);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showError("Dashboard view not found.");
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("create-account-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 440, 956);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void finalizeAccountCreation(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = getPasswordText();
        String confirm = getConfirmPasswordText();

        // Check for empty fields (email not needed for this CSV format)
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            successLabel.setText("All fields are required.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Check if passwords match
        if (!password.equals(confirm)) {
            successLabel.setText("Passwords do not match.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Check for duplicate username or email
        File file = new File("data/users.csv");
        boolean duplicateFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int nextId = 1;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    // Format: ID,Username,Password
                    String existingUsername = parts[1].trim();
                    if (username.equalsIgnoreCase(existingUsername)) {
                        duplicateFound = true;
                        break;
                    }
                    // Track highest ID
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        if (id >= nextId) {
                            nextId = id + 1;
                        }
                    } catch (NumberFormatException e) {
                        // Skip if ID is not a number
                    }
                }
            }

            if (!duplicateFound) {
                // Write new user to CSV with auto-incremented ID
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(nextId + "," + username + "," + password + "\n");
                } catch (IOException e) {
                    successLabel.setText("Error saving account.");
                    successLabel.setStyle("-fx-text-fill: red;");
                    successLabel.setVisible(true);
                    return;
                }
            }
        } catch (IOException e) {
            successLabel.setText("Error reading user data.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        if (duplicateFound) {
            successLabel.setText("Username already exists.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Success message and redirect
        successLabel.setText("Account created successfully!");
        successLabel.setStyle("-fx-background-color: forestgreen;");
        successLabel.setTextFill(Color.FLORALWHITE);
        successLabel.setVisible(true);

        transitionToLogin(event);
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("forgot-pass-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 440, 956);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleSendPasswordReset(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        // Check for empty fields
        if (username.isEmpty() || email.isEmpty()) {
            successLabel.setText("All fields are required.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Simulate sending email (in real app, verify user exists and send actual email)
        successLabel.setText("The email was sent!");
        successLabel.setStyle("-fx-background-color: forestgreen;");
        successLabel.setTextFill(Color.FLORALWHITE);
        successLabel.setVisible(true);
        backToLoginButton.setVisible(true);
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 440, 956);
        stage.setScene(scene);
        stage.show();
    }

    private void transitionToLogin(ActionEvent event) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(loginRoot, 440, 956);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
    }

    // Helper method to get password text from visible field
    private String getPasswordText() {
        if (passwordField.isVisible()) {
            return passwordField.getText().trim();
        } else {
            return textField.getText().trim();
        }
    }

    // Helper method to get confirm password text from visible field
    private String getConfirmPasswordText() {
        if (confirmPasswordField.isVisible()) {
            return confirmPasswordField.getText().trim();
        } else {
            return confirmTextField.getText().trim();
        }
    }

    // Helper method to show error messages
    private void showError(String message) {
        // Try to use errorLabel first (for login screen), then successLabel (for other screens)
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
        } else {
            System.err.println(message);
        }
    }
}