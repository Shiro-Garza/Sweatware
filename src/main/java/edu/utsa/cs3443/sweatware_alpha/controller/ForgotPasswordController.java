package edu.utsa.cs3443.sweatware_alpha.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private Label successLabel;
    @FXML private Button backToLoginButton;

    @FXML
    private void handleSendPasswordReset(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || email.isEmpty()) {
            successLabel.setText("All fields are required.");
            successLabel.setTextFill(Color.RED);
            successLabel.setVisible(true);
            return;
        }

        // Logic to send email would go here
        successLabel.setText("The email was sent!");
        successLabel.setStyle("-fx-background-color: forestgreen; -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 5;");
        successLabel.setVisible(true);

        if (backToLoginButton != null) {
            backToLoginButton.setVisible(true);
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/utsa/cs3443/sweatware_alpha/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}