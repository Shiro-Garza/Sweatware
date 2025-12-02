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

public class DashboardController {

    @FXML private ListView<String> workoutListView;
    @FXML private Label totalWorkoutsLabel;
    @FXML private Label weeklyWorkoutsLabel;
    @FXML private Label monthlyWorkoutsLabel;
    @FXML private Label welcomeLabel; // Optional: If you want to say "Hello, [User]"

    private User currentUser;

    /**
     * This method is called by the LoginController to pass the logged-in user.
     */
    public void initData(User user) {
        this.currentUser = user;
        System.out.println("Dashboard loaded for: " + currentUser.getUsername());

        // Now that we have the user, we can load their specific data
        loadUserWorkouts();
        updateQuickStats();
    }

    private void loadUserWorkouts() {
        workoutListView.getItems().clear();
        List<String[]> allWorkouts = DataManager.readCSV("data/workouts.csv");

        for (String[] row : allWorkouts) {
            // Check if this row belongs to the current user
            if (row.length >= 4 && row[0].equals(currentUser.getUsername())) {
                String type = row[1];
                String reps = row[2];
                String sets = row[3];
                String entry = type + ": " + reps + " reps × " + sets + " sets";
                workoutListView.getItems().add(entry);
            }
        }
    }

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

    @FXML
    private void openWorkoutPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/workout-popup.fxml"));
            Parent root = loader.load();

            // Pass the user to the group
            WorkoutPopupController controller = loader.getController();
            controller.initData(currentUser);

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Workout");
            popupStage.setScene(new Scene(root));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait(); // Pauses here until popup closes

            // Refresh data after popup closes
            loadUserWorkouts();
            updateQuickStats();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/profile-view.fxml"));
            Parent root = loader.load();

            // Pass user to ProfileController
            ProfileController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAll(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/view-all-popup.fxml"));
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

    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}