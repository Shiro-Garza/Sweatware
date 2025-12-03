package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.util.List;

/**
 * Controller for the "View All Workouts" popup in Sweatware.
 * <p>
 * Displays all workouts associated with the currently logged-in user,
 * including type, reps, sets, and date. Data is loaded from the
 * {@code data/workouts.csv} file via {@link DataManager}.
 * </p>
 * <p>This view is typically opened from the dashboard when the user
 * wants to see their complete workout history.</p>
 * @author Aiden Garvett
 * @version final
 */
public class ViewAllWorkoutsController {

    /** List view displaying all workouts for the current user. */
    @FXML private ListView<String> allWorkoutsListView;

    /** The currently logged-in user whose workouts are being displayed. */
    private User currentUser;

    /**
     * Initializes the controller with the current user's data.
     * Called by the {@link DashboardController} when opening this view.
     * @param user the logged-in user
     */
    public void initData(User user) {
        this.currentUser = user;
        loadAllWorkouts();
    }

    /**
     * Loads all workouts for the current user from the CSV file
     * and displays them in the {@link #allWorkoutsListView}.
     * <p>
     * Each workout entry is formatted as:
     * <code>date — type: reps reps × sets sets</code>
     * </p>
     */
    private void loadAllWorkouts() {
        if (allWorkoutsListView != null) {
            allWorkoutsListView.getItems().clear();
            List<String[]> allWorkouts = DataManager.readCSV("data/workouts.csv");

            for (String[] row : allWorkouts) {
                if (row.length >= 5 && row[0].equals(currentUser.getUsername())) {
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
}