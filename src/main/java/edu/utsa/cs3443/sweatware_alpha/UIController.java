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
    private ComboBox<String> workoutTypeCombo;
    @FXML
    private TextField repsField;
    @FXML
    private TextField setsField;
    @FXML
    private Button confrimAddButton;


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

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            successLabel.setText("All fields are required.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Validate username (no commas or line breaks)
        if (username.contains(",") || username.contains("\n") || username.contains("\r")) {
            successLabel.setText("Username cannot contain commas or line breaks.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

        // Validate password (no commas or line breaks)
        if (password.contains(",") || password.contains("\n") || password.contains("\r")) {
            successLabel.setText("Password cannot contain commas or line breaks.");
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

        File file = new File("/data/users.csv");
        boolean duplicateFound = false;
        int nextId = 1;

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String existingUsername = parts[1].trim();
                    if (username.equalsIgnoreCase(existingUsername)) {
                        duplicateFound = true;
                        break;
                    }
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        if (id >= nextId) {
                            nextId = id + 1;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid ID format in CSV: " + parts[0]);
                    }
                }
            }
            reader.close();
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(nextId + "," + username + "," + password);
            writer.newLine(); // ensures proper line break
        } catch (IOException e) {
            e.printStackTrace(); // helpful for debugging
            successLabel.setText("Error saving account.");
            successLabel.setStyle("-fx-text-fill: red;");
            successLabel.setVisible(true);
            return;
        }

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

    @FXML
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
        if (passwordField != null && passwordField.isVisible()) {
            return passwordField.getText().trim();
        } else if (textField != null) {
            return textField.getText().trim();
        }
        return "";
    }

    // Helper method to get confirm password text from visible field
    private String getConfirmPasswordText() {
        if (confirmPasswordField != null && confirmPasswordField.isVisible()) {
            return confirmPasswordField.getText().trim();
        } else if (confirmTextField != null) {
            return confirmTextField.getText().trim();
        }
        return "";
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

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleConfirmAddWorkout(ActionEvent event) {
        String type = workoutTypeCombo.getValue();
        String reps = repsField.getText();
        String sets = setsField.getText();

        if (type == null || reps.isEmpty() || sets.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.");
            alert.show();
            return;
        }

        System.out.println("Workout added: " + type + " - " + reps + " reps x " + sets + " sets");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleViewAll(ActionEvent event) {
        switchScene(event, "WorkoutList.fxml");
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        switchScene(event, "profile-view.fxml");
    }

    @FXML
    public void initialize() {
        if (workoutTypeCombo != null) {
            workoutTypeCombo.getItems().addAll("Push-ups", "Squats", "Plank", "Burpees", "Lunges", "Sit-ups");
        }
    }
    @FXML
    public void transitionToDashbord(ActionEvent event){
        switchScene(event, "dashboard-view.fxml");
    }
}