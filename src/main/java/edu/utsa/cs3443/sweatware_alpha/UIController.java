package edu.utsa.cs3443.sweatware_alpha;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javax.swing.*;


public class UIController {

    // Login and registration fields
    @FXML private PasswordField passwordField;
    @FXML private TextField textField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmTextField;
    @FXML private CheckBox confirmPasswordToggle;
    @FXML private Label successLabel;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private Button backToLoginButton;
    @FXML private Label errorLabel;

    // Workout fields
    @FXML private ComboBox<String> workoutTypeCombo;
    @FXML private TextField repsField;
    @FXML private TextField setsField;
    @FXML private Button confrimAddButton;
    @FXML private VBox workoutListContainer;
    @FXML private ListView<String> workoutListView;
    @FXML private Label totalWorkoutsLabel;
    @FXML private Label weeklyWorkoutsLabel;
    @FXML private Label monthlyWorkoutsLabel;
    @FXML private ListView<String> allWorkoutsListView;


    // Profile fields
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField weightField;
    @FXML private TextField genderField;
    @FXML private TextArea contactInfoArea;

    // Static session data
    private static String currentUsername;
    private static String currentEmail;
    private static String currentAge;
    private static String currentGender;
    private static String currentWeight;

    public static void setCurrentUser(String username, String email, String age, String gender, String weight) {
        currentUsername = username;
        currentEmail = email;
        currentAge = age;
        currentGender = gender;
        currentWeight = weight;
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

        String username = (usernameField != null && usernameField.getText() != null)
                ? usernameField.getText().trim() : "";
        String password = getPasswordText();

        if (username.isEmpty() || password.isEmpty()) {

            showError("please enter a username and password.");
            return;
        }

        List<String[]> users = readCSV("data/users.csv");
        boolean authenticated = false;

        for (String[] user : users) {
            if (user.length >= 2) {
                String storedUsername = user[0].trim();
                String storedPassword = user[1].trim();

                System.out.println("Checking user:");
                System.out.println("Entered username: '" + username + "'");
                System.out.println("Stored username: '" + storedUsername + "'");
                System.out.println("Entered password: '" + password + "'");
                System.out.println("Stored password: '" + storedPassword + "'");

                if (username.equals(storedUsername) && password.equals(storedPassword)) {
                    String storedEmail = user.length >= 3 ? user[2].trim() : "";
                    String storedAge = user.length >= 4 ? user[3].trim() : "";
                    String storedGender = user.length >= 5 ? user[4].trim() : "";
                    String storedWeight = user.length >= 6 ? user[5].trim() : "";

                    setCurrentUser(storedUsername, storedEmail, storedAge, storedGender, storedWeight);
                    authenticated = true;
                    break;
                }
            }
        }

        if (authenticated) {
            Parent root = FXMLLoader.load(getClass().getResource("dashboard-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 440, 956));
            stage.show();
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("create-account-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 440, 956));
        stage.show();
    }

    @FXML
    private void finalizeAccountCreation(ActionEvent event) {
        // Null-safe reads to prevent NPEs when fields are not present/visible
        String username = (usernameField != null && usernameField.getText() != null)
                ? usernameField.getText().trim() : "";
        String password = getPasswordText();
        String confirm = getConfirmPasswordText();
        String email = (emailField != null && emailField.getText() != null)
                ? emailField.getText().trim() : "";
        String age = ageField != null ? ageField.getText().trim() : "";
        String gender = genderCombo != null ? genderCombo.getValue() : "";
        String weight = weightField != null ? weightField.getText().trim() : "";

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) {

            showError("please enter a username and password.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        List<String[]> users = readCSV("data/users.csv");
        for (String[] user : users) {
            if (user.length >= 1 && username.equalsIgnoreCase(user[0].trim())) {
                showError("Username already exists.");
                return;
            }
        }

        appendToCSV("data/users.csv", username, password, email, age, gender, weight);

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
        stage.setScene(new Scene(root, 440, 956));
        stage.show();
    }

    @FXML
    private void handleSendPasswordReset(ActionEvent event) {

        String username = (usernameField != null && usernameField.getText() != null)
                ? usernameField.getText().trim() : "";
        String email = (emailField != null && emailField.getText() != null)
                ? emailField.getText().trim() : "";

        if (username.isEmpty() || email.isEmpty()) {

            showError("please enter a username and password.");
            return;
        }

        successLabel.setText("The email was sent!");
        successLabel.setStyle("-fx-background-color: forestgreen;");
        successLabel.setTextFill(Color.FLORALWHITE);
        successLabel.setVisible(true);
        if (backToLoginButton != null) {
            backToLoginButton.setVisible(true);
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 440, 956));
        stage.show();
    }

    @FXML
    private void transitionToLogin(ActionEvent event) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loginRoot, 440, 956));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
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
        appendWorkout(currentUsername, type, reps, sets);

        // Close the popup
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        switchScene(event, "profile-view.fxml");
    }

    @FXML
    public void initialize() {
        // Populate workout type dropdown if present
        if (workoutTypeCombo != null) {
            workoutTypeCombo.getItems().addAll("Push-ups", "Squats", "Plank", "Burpees", "Lunges", "Sit-ups");
        }
        if (workoutListView != null) {
            List<String[]> allWorkouts = readCSV("data/workouts.csv");

            for (String[] row : allWorkouts) {
                if (row.length >= 4 && row[0].equals(currentUsername)) {
                    String type = row[1];
                    String reps = row[2];
                    String sets = row[3];
                    String entry = type + ": " + reps + " reps × " + sets + " sets";
                    workoutListView.getItems().add(entry);
                }
            }
        }
        loadUserWorkouts();
        updateQuickStats();

        // Populate gender combo box if present
        if (genderCombo != null) {
            genderCombo.getItems().addAll("Male", "Female", "Other");
        }

        // Refresh session data from CSV
        List<String[]> users = readCSV("data/users.csv");
        for (String[] user : users) {
            if (user.length >= 6 && user[0].trim().equals(currentUsername)) {
                currentEmail = user[2].trim();
                currentAge = user[3].trim();
                currentGender = user[4].trim();
                currentWeight = user[5].trim();
                break;
            }
        }

        // Populate profile fields if present
        if (usernameField != null) usernameField.setText(currentUsername);
        if (emailField != null) emailField.setText(currentEmail);
        if (ageField != null) ageField.setText(currentAge);
        if (weightField != null) weightField.setText(currentWeight);
        if (genderField != null) genderField.setText(currentGender);
        if (contactInfoArea != null) contactInfoArea.setText(currentEmail);
    }

    // Reusable CSV reader
    public List<String[]> readCSV(String path) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);

        System.out.println("Attempting to read file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.err.println("File not found.");
            return rows;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "");
                System.out.println("Read line: " + line);
                if (!line.trim().isEmpty()) {
                    rows.add(line.split(","));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        System.out.println("Total lines read: " + rows.size());
        return rows;
    }

    // Reusable CSV appender
    public void appendToCSV(String path, String... values) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(String.join(",", values));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    // Reusable CSV overwriter
    public void writeCSV(String path, List<String[]> rows) {
        File file = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            System.out.println("CSV successfully updated.");
        } catch (IOException e) {
            System.err.println("Error overwriting CSV: " + e.getMessage());
        }
    }

    @FXML
    public void transitionToDashbord(ActionEvent event) {
        switchScene(event, "dashboard-view.fxml");
    }
    private String getPasswordText() {
        if (passwordField != null && passwordField.isVisible()) {
            return passwordField.getText().trim();
        } else if (textField != null && textField.isVisible()) {
            return textField.getText().trim();
        }
        return "";
    }
    private void showError(String message) {
        // Always prefer dedicated error label when available
        if (errorLabel != null) {
            errorLabel.setText(message);
            // Ensure styling: red background, white text, padding, rounded corners
            errorLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 5;");
            errorLabel.setVisible(true);
            return;
        }

        // Fallback to successLabel if present (e.g., other screens)
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 5;");
            successLabel.setVisible(true);
            return;
        }

        // Last resort: log to stderr
        System.err.println(message);
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
    private String getConfirmPasswordText() {
        if (confirmPasswordField != null && confirmPasswordField.isVisible()) {
            return confirmPasswordField.getText().trim();
        } else if (confirmTextField != null && confirmTextField.isVisible()) {
            return confirmTextField.getText().trim();
        }
        return "";
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        String updatedAge = ageField.getText().trim();
        String updatedGender = genderField.getText().trim();
        String updatedWeight = weightField.getText().trim();

        List<String[]> users = readCSV("data/users.csv");
        boolean updated = false;

        for (int i = 0; i < users.size(); i++) {
            String[] user = users.get(i);
            if (user.length >= 2 && user[0].trim().equals(currentUsername)) {
                System.out.println("Match found. Updating user: " + currentUsername);

                // Ensure array has 6 fields
                if (user.length < 6) {
                    user = java.util.Arrays.copyOf(user, 6);
                }

                user[3] = updatedAge;
                user[4] = updatedGender;
                user[5] = updatedWeight;

                users.set(i, user); // Replace in list
                updated = true;
                break;
            }
        }

        if (updated) {
            writeCSV("data/users.csv", users);
            showConfirmation("Profile updated successfully!");

            // Update session
            currentAge = updatedAge;
            currentGender = updatedGender;
            currentWeight = updatedWeight;
        } else {
            showError("Could not find user to update.");
        }
    }
    private void showConfirmation(String message) {
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setStyle("-fx-background-color: forestgreen;");
            successLabel.setTextFill(Color.FLORALWHITE);
            successLabel.setVisible(true);
        } else {
            System.out.println(message);
        }
    }
    @FXML
    private void openWorkoutPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("workout-popup.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Workout");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setResizable(false);
            popupStage.showAndWait();
            loadUserWorkouts();
            updateQuickStats();

            // ✅ Refresh workout list after popup closes
            loadUserWorkouts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Append to workouts.csv
    public void appendWorkout(String username, String type, String reps, String sets) {
        String date = LocalDate.now().toString(); // e.g. "2025-11-18"
        appendToCSV("data/workouts.csv", username, type, reps, sets, date);
    }
    public void loadUserWorkouts() {
        if (workoutListView != null) {
            workoutListView.getItems().clear();

            List<String[]> allWorkouts = readCSV("data/workouts.csv");
            for (String[] row : allWorkouts) {
                if (row.length >= 4 && row[0].equals(currentUsername)) {
                    String type = row[1];
                    String reps = row[2];
                    String sets = row[3];
                    String entry = type + ": " + reps + " reps × " + sets + " sets";
                    workoutListView.getItems().add(entry);
                }
            }
        }
    }
    public void updateQuickStats() {
        if (totalWorkoutsLabel == null || weeklyWorkoutsLabel == null || monthlyWorkoutsLabel == null) return;

        List<String[]> allWorkouts = readCSV("data/workouts.csv");
        int total = 0, weekly = 0, monthly = 0;

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        YearMonth currentMonth = YearMonth.from(today);

        for (String[] row : allWorkouts) {
            if (row.length >= 5 && row[0].equals(currentUsername)) {
                total++;

                LocalDate workoutDate;
                try {
                    workoutDate = LocalDate.parse(row[4]);
                } catch (DateTimeParseException e) {
                    continue;
                }

                if (!workoutDate.isBefore(weekStart)) {
                    weekly++;
                }

                if (YearMonth.from(workoutDate).equals(currentMonth)) {
                    monthly++;
                }
            }
        }

        totalWorkoutsLabel.setText("Total Workouts: " + total);
        weeklyWorkoutsLabel.setText("This Week: " + weekly);
        monthlyWorkoutsLabel.setText("This Month: " + monthly);
    }
    public void loadAllWorkoutsForPopup() {
        if (allWorkoutsListView != null) {
            allWorkoutsListView.getItems().clear();
            List<String[]> allWorkouts = readCSV("data/workouts.csv");

            for (String[] row : allWorkouts) {
                if (row.length >= 5 && row[0].equals(currentUsername)) {
                    String type = row[1];
                    String reps = row[2];
                    String sets = row[3];
                    String date = row[4];
                    String entry = date + " — " + type + ": " + reps + " reps × " + sets + " sets";
                    allWorkoutsListView.getItems().add(entry);
                }
            }
        }
    }
    @FXML
    private void handleViewAll(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view-all-popup.fxml"));
            Parent popupRoot = loader.load();

            // Load workouts into the popup
            UIController controller = loader.getController();
            controller.loadAllWorkoutsForPopup();

            Stage popupStage = new Stage();
            popupStage.setTitle("All Workouts");
            popupStage.setScene(new Scene(popupRoot));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}