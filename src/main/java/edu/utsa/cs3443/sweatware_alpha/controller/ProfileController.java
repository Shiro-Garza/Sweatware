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

/**
 * Controller for the Sweatware profile view.
 * <p>
 * Allows users to view and update their profile information
 * (age, gender, weight, and contact details). Updates are persisted
 * to the CSV file via {@link DataManager} and reflected in the
 * current {@link User} session object.
 * </p>
 * <p>Provides navigation back to the dashboard after saving changes.</p>
 * @author Aiden Garvett
 * @version final
 */
public class ProfileController {

    /** Text field displaying the username (read-only). */
    @FXML private TextField usernameField;

    /** Text field for editing the user's age. */
    @FXML private TextField ageField;

    /** Text field for editing the user's gender. */
    @FXML private TextField genderField;

    /** Text field for editing the user's weight. */
    @FXML private TextField weightField;

    /** Text area for displaying the user's contact information (email). */
    @FXML private TextArea contactInfoArea;

    /** The currently logged-in user whose profile is being edited. */
    private User currentUser;

    /**
     * Initializes the profile view with the current user's data.
     * Called by the {@link DashboardController} when navigating to this view.
     * @param user the logged-in user whose profile is being displayed
     */
    public void initData(User user) {
        this.currentUser = user;

        if (usernameField != null) usernameField.setText(currentUser.getUsername());
        if (ageField != null) ageField.setText(currentUser.getAge());
        if (genderField != null) genderField.setText(currentUser.getGender());
        if (weightField != null) weightField.setText(currentUser.getWeight());
        if (contactInfoArea != null) contactInfoArea.setText(currentUser.getEmail());
    }

    /**
     * Handles saving profile updates (age, gender, weight).
     * <p>
     * Reads all users from the CSV file, finds the current user row,
     * updates the relevant fields, writes changes back to the CSV,
     * and updates the session {@link User} object.
     * </p>
     * @param event the action event triggered by the "Save" button
     */
    @FXML
    private void handleSaveProfile(ActionEvent event) {
        String updatedAge = ageField.getText().trim();
        String updatedGender = genderField.getText().trim();
        String updatedWeight = weightField.getText().trim();

        List<String[]> users = DataManager.readCSV("data/users.csv");
        boolean updated = false;

        for (int i = 0; i < users.size(); i++) {
            String[] row = users.get(i);
            if (row.length >= 2 && row[0].trim().equals(currentUser.getUsername())) {

                if (row.length < 6) {
                    String[] newRow = new String[6];
                    System.arraycopy(row, 0, newRow, 0, row.length);
                    for (int k = row.length; k < 6; k++) newRow[k] = "";
                    row = newRow;
                }

                row[3] = updatedAge;
                row[4] = updatedGender;
                row[5] = updatedWeight;

                users.set(i, row);
                updated = true;
                break;
            }
        }

        if (updated) {
            DataManager.writeCSV("data/users.csv", users);

            currentUser.setAge(updatedAge);
            currentUser.setGender(updatedGender);
            currentUser.setWeight(updatedWeight);

            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Could not find user to update.");
        }
    }

    /**
     * Navigates back to the dashboard view, passing the updated
     * {@link User} object to keep the session in sync.
     * @param event the action event triggered by the "Back" button
     */
    @FXML
    private void transitionToDashbord(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/utsa/cs3443/sweatware_alpha/dashboard-view.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog with the given title and content.
     * @param title   the title of the alert window
     * @param content the message to display
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}