package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller for the Sweatware dashboard view.
 * <p>
 * Displays a user's workout history, quick statistics (weekly, monthly, total),
 * and provides navigation to other views such as workout entry, profile, and
 * viewing all workouts.
 * </p>
 *
 * <p>Data is loaded from CSV files via {@link DataManager}.</p>
 *
 * @author Aiden Garvett
 * @version final
 */
public class DashboardController {

    /** List view displaying the user's workouts. */
    @FXML private ListView<String> workoutListView;

    /** Label showing the total number of workouts. */
    @FXML private Label totalWorkoutsLabel;

    /** Label showing the number of workouts completed this week. */
    @FXML private Label weeklyWorkoutsLabel;

    /** Label showing the number of workouts completed this month. */
    @FXML private Label monthlyWorkoutsLabel;

    /** Optional label to greet the user (e.g., "Hello, [User]"). */
    @FXML private Label welcomeLabel;

    /** The currently logged-in user. */
    private User currentUser;

    /**
     * Initializes the dashboard with the logged-in user's data.
     * Called by the {@code LoginController} after successful login.
     *
     * @param user the authenticated user
     */
    public void initData(User user) {
        this.currentUser = user;
        System.out.println("Dashboard loaded for: " + currentUser.getUsername());

        loadUserWorkouts();
        updateQuickStats();
    }

    /**
     * Loads the current user's workouts from the CSV file and
     * displays them in the {@link #workoutListView}.
     */
    private void loadUserWorkouts() {
        workoutListView.getItems().clear();
        List<String[]> allWorkouts = DataManager.readCSV("data/workouts.csv");

        for (String[] row : allWorkouts) {
            if (row.length >= 4 && row[0].equals(currentUser.getUsername())) {
                String type = row[1];
                String reps = row[2];
                String sets = row[3];
                String entry = type + ": " + reps + " reps × " + sets + " sets";
                workoutListView.getItems().add(entry);
            }
        }
    }

    /**
     * Updates quick statistics for the current user:
     * <ul>
     *   <li>Total workouts</li>
     *   <li>Workouts completed in the last 7 days</li>
     *   <li>Workouts completed in the current month</li>
     * </ul>
     * Invalid or unparsable dates are ignored.
     */
    private void updateQuickStats() {
        List<String[]> allWorkouts = DataManager.readCSV("data/workouts.csv");
        int total = 0, weekly = 0, monthly = 0;

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        YearMonth currentMonth = YearMonth.from(today);

        for (String[] row : allWorkouts) {
            if (row.length >= 5 && row[0].equals(currentUser.getUsername())) {
                total++;
                try {
                    LocalDate workoutDate = LocalDate.parse(row[4]);

                    if (!workoutDate.isBefore(weekStart)) {
                        weekly++;
                    }
                    if (YearMonth.from(workoutDate).equals(currentMonth)) {
                        monthly++;
                    }
                } catch (DateTimeParseException e) {
                    // Ignore invalid dates
                }
            }
        }

        totalWorkoutsLabel.setText("Total Workouts: " + total);
        weeklyWorkoutsLabel.setText("This Week: " + weekly);
        monthlyWorkoutsLabel.setText("This Month: " + monthly);
    }

    /**
     * Opens a popup window for adding a new workout.
     * After the popup closes, the dashboard refreshes the user's workouts and stats.
     *
     * @param event the action event triggered by the "Add Workout" button
     */
    @FXML
    private void openWorkoutPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/utsa/cs3443/sweatware_alpha/workout-popup.fxml"));
            Parent root = loader.load();

            WorkoutPopupController controller = loader.getController();
            controller.initData(currentUser);

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Workout");
            popupStage.setScene(new Scene(root));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait();

            loadUserWorkouts();
            updateQuickStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the profile view for the current user.
     *
     * @param event the action event triggered by the "Profile" button
     */
    @FXML
    private void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/utsa/cs3443/sweatware_alpha/profile-view.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a popup window displaying all workouts for the current user.
     *
     * @param event the action event triggered by the "View All" button
     */
    @FXML
    private void handleViewAll(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/utsa/cs3443/sweatware_alpha/view-all-popup.fxml"));
            Parent root = loader.load();

            ViewAllWorkoutsController controller = loader.getController();
            controller.initData(currentUser);

            Stage popupStage = new Stage();
            popupStage.setTitle("All Workouts");
            popupStage.setScene(new Scene(root));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the user to the login view.
     *
     * @param event the action event triggered by the "Logout" or "Back to Login" button
     * @throws IOException if the FXML file cannot be loaded
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(
                "/edu/utsa/cs3443/sweatware_alpha/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}