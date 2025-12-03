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
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.List;

/**
 * Controller for the "Create Account" view in Sweatware.
 * <p>
 * Handles user input validation, account creation, password visibility toggles,
 * and navigation back to the login screen.
 * </p>
 *
 * <p>Accounts are stored in a CSV file with the format:
 * <code>username, password, email, age, gender, weight</code>.</p>
 *
 * @author Aiden Garvett
 * @version final
 */
public class CreateAccountController {

    /** Text field for entering a username. */
    @FXML private TextField usernameField;

    /** Text field for entering an email address. */
    @FXML private TextField emailField;

    // --- Password controls ---
    /** Hidden password field. */
    @FXML private PasswordField passwordField;

    /** Visible password field (shown when toggle is active). */
    @FXML private TextField textField;

    /** Checkbox to toggle password visibility. */
    @FXML private CheckBox showPasswordCheckBox;

    // --- Confirm password controls ---
    /** Hidden confirm password field. */
    @FXML private PasswordField confirmPasswordField;

    /** Visible confirm password field (shown when toggle is active). */
    @FXML private TextField confirmTextField;

    /** Checkbox to toggle confirm password visibility. */
    @FXML private CheckBox confirmPasswordToggle;

    /** Label used to display success or error messages. */
    @FXML private Label successLabel;

    /**
     * Validates input fields, checks for duplicate usernames,
     * creates a new account, and transitions back to login.
     *
     * @param event the action event triggered by the "Create Account" button
     */
    @FXML
    private void finalizeAccountCreation(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = getPasswordText();
        String confirm = getConfirmPasswordText();

        // Validation checks
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) {
            showMessage("All fields are required.", true);
            return;
        }

        if (!password.equals(confirm)) {
            showMessage("Passwords do not match.", true);
            return;
        }

        // Check for duplicate username
        List<String[]> users = DataManager.readCSV("data/users.csv");
        for (String[] user : users) {
            if (user.length >= 1 && username.equalsIgnoreCase(user[0].trim())) {
                showMessage("Username already exists.", true);
                return;
            }
        }

        // Append new account to CSV
        DataManager.appendToCSV("data/users.csv", username, password, email, "", "", "");
        showMessage("Account created successfully!", false);

        // Transition back to login after delay
        transitionToLoginWithDelay(event);
    }

    /**
     * Immediately transitions back to the login view.
     *
     * @param event the action event triggered by navigation
     */
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

    /**
     * Transitions back to the login view after a 2-second delay.
     *
     * @param event the action event triggered by navigation
     */
    private void transitionToLoginWithDelay(ActionEvent event) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> transitionToLogin(event));
        delay.play();
    }

    // --- Helper Methods for Password Toggles ---

    /** Toggles visibility of the main password field. */
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

    /** Toggles visibility of the confirm password field. */
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

    /**
     * Retrieves the current password text, regardless of visibility toggle.
     *
     * @return the trimmed password string
     */
    private String getPasswordText() {
        return passwordField.isVisible() ? passwordField.getText().trim() : textField.getText().trim();
    }

    /**
     * Retrieves the current confirm password text, regardless of visibility toggle.
     *
     * @return the trimmed confirm password string
     */
    private String getConfirmPasswordText() {
        return confirmPasswordField.isVisible() ? confirmPasswordField.getText().trim() : confirmTextField.getText().trim();
    }

    /**
     * Displays a message in the success label, styled as either
     * an error (red background) or success (green background).
     *
     * @param message the message to display
     * @param isError true if the message is an error, false if success
     */
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