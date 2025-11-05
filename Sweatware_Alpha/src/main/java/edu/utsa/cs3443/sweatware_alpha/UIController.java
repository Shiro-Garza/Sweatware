package edu.utsa.cs3443.sweatware_alpha;

import com.sun.net.httpserver.Authenticator;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.*;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

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
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            textField.setVisible(false);
        }
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        boolean show = confirmPasswordToggle.isSelected();
        if (show) {
            confirmTextField.setText(confirmPasswordField.getText());
        } else {
            confirmPasswordField.setText(confirmTextField.getText());
        }
        confirmTextField.setVisible(show);
        confirmTextField.setManaged(show);
        confirmPasswordField.setVisible(!show);
        confirmPasswordField.setManaged(!show);
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("create-account-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void finalizeAccountCreation(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
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
        File file = new File("data/userInfo.csv");
        boolean duplicateFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String existingUsername = parts[0].trim();
                    String existingEmail = parts[1].trim();
                    if (username.equalsIgnoreCase(existingUsername) || email.equalsIgnoreCase(existingEmail)) {
                        duplicateFound = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            successLabel.setText("Error reading user data.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        if (duplicateFound) {
            successLabel.setText("Username or email already exists.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Write new user to CSV
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(username + "," + email + "," + password + "\n");
        } catch (IOException e) {
            successLabel.setText("Error saving account.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Success message and redirect
        successLabel.setText("Account created successfully!");
        successLabel.setStyle("-fx-text-fill: green;");
        successLabel.setVisible(true);

        transitionToLogin(event);
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) throws IOException {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        Parent root = FXMLLoader.load(getClass().getResource("forgot-pass-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

        if (username.isEmpty() || email.isEmpty()) {
            successLabel.setText("All fields are required.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        File file = new File("data/userInfo.csv");
        boolean duplicateFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String existingUsername = parts[0].trim();
                    String existingEmail = parts[1].trim();
                    if (username.equalsIgnoreCase(existingUsername) || email.equalsIgnoreCase(existingEmail)) {
                        duplicateFound = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            successLabel.setText("Error reading user data.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        if (duplicateFound) {
            successLabel.setText("Email sent");
            successLabel.setStyle("-fx-text-fill: green;");
            successLabel.setVisible(true);
            return;
        }

        transitionToLogin(event);
    }

    private void transitionToLogin(ActionEvent event){
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loginRoot));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
    }


}
