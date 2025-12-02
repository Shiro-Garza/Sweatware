package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.util.List;

public class ViewAllWorkoutsController {

    @FXML private ListView<String> allWorkoutsListView;
    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        loadAllWorkouts();
    }

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