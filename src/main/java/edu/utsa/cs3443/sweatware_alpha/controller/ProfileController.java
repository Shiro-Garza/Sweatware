package edu.utsa.cs3443.sweatware_alpha.controller;

import edu.utsa.cs3443.sweatware_alpha.model.User;
import edu.utsa.cs3443.sweatware_alpha.services.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField ageField;
    @FXML private TextField genderField;
    @FXML private TextField weightField;
    @FXML private TextArea contactInfoArea;

    private User currentUser;

    /**
     * Receives the User object from DashboardController
     */
    public void initData(User user) {
        this.currentUser = user;

        // Populate the fields with the user's current data
        if (usernameField != null) usernameField.setText(currentUser.getUsername());
        if (ageField != null) ageField.setText(currentUser.getAge());
        if (genderField != null) genderField.setText(currentUser.getGender());
        if (weightField != null) weightField.setText(currentUser.getWeight());
        if (contactInfoArea != null) contactInfoArea.setText(currentUser.getEmail());
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        String updatedAge = ageField.getText().trim();
        String updatedGender = genderField.getText().trim();
        String updatedWeight = weightField.getText().trim();

        // 1. Read all users
        List<String[]> users = DataManager.readCSV("data/users.csv");
        boolean updated = false;

        // 2. Find and update the specific user row
        for (int i = 0; i < users.size(); i++) {
            String[] row = users.get(i);
            // row[0] is username
            if (row.length >= 2 && row[0].trim().equals(currentUser.getUsername())) {

                // Ensure array is large enough to hold all fields
                if (row.length < 6) {
                    // resize array if needed to [username, pass, email, age, gender, weight]
                    String[] newRow = new String[6];
                    System.arraycopy(row, 0, newRow, 0, row.length);
                    // Fill nulls with empty strings to avoid null pointers
                    for(int k=row.length; k<6; k++) newRow[k] = "";
                    row = newRow;
                }

                row[3] = updatedAge;
                row[4] = updatedGender;
                row[5] = updatedWeight;

                users.set(i, row); // Replace the row in the list
                updated = true;
                break;
            }
        }

        // 3. Write changes back to CSV and update the Session Object
        if (updated) {
            DataManager.writeCSV("data/users.csv", users);

            // Update the local User object so the session stays in sync!
            currentUser.setAge(updatedAge);
            currentUser.setGender(updatedGender);
            currentUser.setWeight(updatedWeight);

            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Could not find user to update.");
        }
    }

    @FXML
    private void transitionToDashbord(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/dashboard-view.fxml"));
            Parent root = loader.load();

            // Pass the updated user object BACK to the dashboard
            DashboardController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}