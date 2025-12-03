package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
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

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Sweatware login view.
 * <p>
 * Handles user authentication, password visibility toggling,
 * and navigation to other views such as account creation,
 * password recovery, and the dashboard.
 * </p>
 * <p>User credentials are validated against data stored in
 * {@code data/users.csv} via {@link DataManager}.</p>
 * @author Aiden Garvett
 * @version final
 */
public class LoginController {

    /** Text field for entering the username. */
    @FXML private TextField usernameField;

    /** Hidden password field. */
    @FXML private PasswordField passwordField;

    /** Visible password field (shown when toggle is active). */
    @FXML private TextField textField;

    /** Checkbox to toggle password visibility. */
    @FXML private CheckBox showPasswordCheckBox;

    /** Label used to display error messages. */
    @FXML private Label errorLabel;

    /**
     * Handles login attempts by validating the entered username and password
     * against stored user data. If authentication succeeds, loads the dashboard.
     * @param event the action event triggered by the "Login" button
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = getPasswordText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        List<String[]> users = DataManager.readCSV("data/users.csv");
        boolean authenticated = false;
        User loggedInUser = null;

        for (String[] userRow : users) {
            if (userRow.length >= 2) {
                String storedUsername = userRow[0].trim();
                String storedPassword = userRow[1].trim();

                if (username.equals(storedUsername) && password.equals(storedPassword)) {
                    String email = userRow.length >= 3 ? userRow[2].trim() : "";
                    String age = userRow.length >= 4 ? userRow[3].trim() : "";
                    String gender = userRow.length >= 5 ? userRow[4].trim() : "";
                    String weight = userRow.length >= 6 ? userRow[5].trim() : "";

                    loggedInUser = new User(storedUsername, email, age, gender, weight);
                    authenticated = true;
                    break;
                }
            }
        }

        if (authenticated) {
            loadDashboard(event, loggedInUser);
        } else {
            showError("Invalid username or password.");
        }
    }

    /**
     * Loads the dashboard view and passes the authenticated user
     * to the {@link DashboardController}.
     * @param event the action event triggered by login
     * @param user  the authenticated user
     */
    private void loadDashboard(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/utsa/cs3443/sweatware_alpha/dashboard-view.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.initData(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard.");
        }
    }

    /**
     * Toggles visibility of the password field between hidden and visible.
     */
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

    /**
     * Navigates to the "Create Account" view.
     * @param event the action event triggered by the "Create Account" button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleCreateAccount(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/sweatware_alpha/create-account-view.fxml");
    }

    /**
     * Navigates to the "Forgot Password" view.
     * @param event the action event triggered by the "Forgot Password" button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/sweatware_alpha/forgot-pass-view.fxml");
    }

    /**
     * Switches the current scene to the specified FXML view.
     * @param event    the action event triggering the scene change
     * @param fxmlPath the path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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
     * Displays an error message in the error label.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}