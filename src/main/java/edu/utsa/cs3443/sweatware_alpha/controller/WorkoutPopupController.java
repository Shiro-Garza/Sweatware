package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class WorkoutPopupController {

    @FXML private TextField workoutText;
    @FXML private TextField repsField;
    @FXML private TextField setsField;

    private User currentUser;

    /**
     * Receives the User object so we know WHO is adding the workout.
     */
    public void initData(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleConfirmAddWorkout(ActionEvent event) {
        String type = workoutText.getText().trim();
        String reps = repsField.getText().trim();
        String sets = setsField.getText().trim();

        if (type.isEmpty() || reps.isEmpty() || sets.isEmpty()) {
            showAlert("Warning", "Please fill in all fields.");
            return;
        }

        // Save to CSV using DataManager
        // Format: username, type, reps, sets, date
        String date = LocalDate.now().toString();

        DataManager.appendToCSV("data/workouts.csv",
                currentUser.getUsername(),
                type,
                reps,
                sets,
                date
        );

        // Close the popup
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}