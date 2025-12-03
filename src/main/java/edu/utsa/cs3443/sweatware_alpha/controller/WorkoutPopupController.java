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

/**
 * Controller for the "Add Workout" popup in Sweatware.
 * <p>
 * Allows the current user to add a new workout entry by specifying
 * type, reps, and sets. The workout is saved to the CSV file via
 * {@link DataManager}, including the current date.
 * </p>
 * <p>This popup is typically opened from the dashboard and closes
 * automatically after a workout is added.</p>
 * @author Aiden Garvett
 * @version final
 */
public class WorkoutPopupController {

    /** Text field for entering the workout type (e.g., push-ups, squats). */
    @FXML private TextField workoutText;

    /** Text field for entering the number of repetitions. */
    @FXML private TextField repsField;

    /** Text field for entering the number of sets. */
    @FXML private TextField setsField;

    /** The currently logged-in user who is adding the workout. */
    private User currentUser;

    /**
     * Initializes the controller with the current user's data.
     * Called by the {@link DashboardController} when opening this popup.
     * @param user the logged-in user
     */
    public void initData(User user) {
        this.currentUser = user;
    }

    /**
     * Handles the confirmation of adding a workout.
     * <p>
     * Validates that all fields are filled, then appends the workout
     * to {@code data/workouts.csv} in the format:
     * <code>username, type, reps, sets, date</code>.
     * </p>
     * After saving, the popup window is closed.
     * @param event the action event triggered by the "Confirm" button
     */
    @FXML
    private void handleConfirmAddWorkout(ActionEvent event) {
        String type = workoutText.getText().trim();
        String reps = repsField.getText().trim();
        String sets = setsField.getText().trim();

        if (type.isEmpty() || reps.isEmpty() || sets.isEmpty()) {
            showAlert("Warning", "Please fill in all fields.");
            return;
        }

        String date = LocalDate.now().toString();

        DataManager.appendToCSV("data/workouts.csv",
                currentUser.getUsername(),
                type,
                reps,
                sets,
                date
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Displays an alert dialog with the given title and content.
     * @param title   the title of the alert window
     * @param content the message to display
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}