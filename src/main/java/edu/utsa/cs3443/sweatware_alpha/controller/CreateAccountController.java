package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class CreateAccountController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;

    // Password controls
    @FXML private PasswordField passwordField;
    @FXML private TextField textField; // Visible password
    @FXML private CheckBox showPasswordCheckBox;

    // Confirm Password controls
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmTextField; // Visible confirm password
    @FXML private CheckBox confirmPasswordToggle;

    @FXML private Label successLabel; // We will use this for both errors (Red) and success (Green)

    @FXML
    private void finalizeAccountCreation(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = getPasswordText();
        String confirm = getConfirmPasswordText();

        // 1. Basic Validation
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) {
            showMessage("All fields are required.", true);
            return;
        }

        if (!password.equals(confirm)) {
            showMessage("Passwords do not match.", true);
            return;
        }

        // 2. Check if username already exists
        List<String[]> users = DataManager.readCSV("data/users.csv");
        for (String[] user : users) {
            if (user.length >= 1 && username.equalsIgnoreCase(user[0].trim())) {
                showMessage("Username already exists.", true);
                return;
            }
        }

        // 3. Create the account (Format: username, password, email, age, gender, weight)
        // We initialize age, gender, weight as empty strings for now.
        DataManager.appendToCSV("data/users.csv", username, password, email, "", "", "");

        showMessage("Account created successfully!", false);

        // 4. Automatically go back to login after 2 seconds
        transitionToLoginWithDelay(event);
    }

    @FXML
    private void transitionToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transitionToLoginWithDelay(ActionEvent event) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> transitionToLogin(event));
        delay.play();
    }

    // --- Helper Methods for Password Toggles ---

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

    private String getPasswordText() {
        return passwordField.isVisible() ? passwordField.getText().trim() : textField.getText().trim();
    }

    private String getConfirmPasswordText() {
        return confirmPasswordField.isVisible() ? confirmPasswordField.getText().trim() : confirmTextField.getText().trim();
    }

    private void showMessage(String message, boolean isError) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        if (isError) {
            successLabel.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-padding: 5; -fx-background-radius: 5;");
        } else {
            successLabel.setStyle("-fx-text-fill: white; -fx-background-color: forestgreen; -fx-padding: 5; -fx-background-radius: 5;");
        }
    }
}