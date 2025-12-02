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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField textField; // For visible password
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = getPasswordText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        // Use DataManager to read users
        List<String[]> users = DataManager.readCSV("data/users.csv");
        boolean authenticated = false;
        User loggedInUser = null;

        for (String[] userRow : users) {
            // Ensure row has enough columns (username, password)
            if (userRow.length >= 2) {
                String storedUsername = userRow[0].trim();
                String storedPassword = userRow[1].trim();

                if (username.equals(storedUsername) && password.equals(storedPassword)) {
                    // Extract user data safely
                    String email = userRow.length >= 3 ? userRow[2].trim() : "";
                    String age = userRow.length >= 4 ? userRow[3].trim() : "";
                    String gender = userRow.length >= 5 ? userRow[4].trim() : "";
                    String weight = userRow.length >= 6 ? userRow[5].trim() : "";

                    // Create the User object
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

    private void loadDashboard(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/dashboard-view.fxml"));
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
    private void handleCreateAccount(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/sweatware_alpha/create-account-view.fxml");
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/sweatware_alpha/forgot-pass-view.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String getPasswordText() {
        if (passwordField.isVisible()) {
            return passwordField.getText().trim();
        } else {
            return textField.getText().trim();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}