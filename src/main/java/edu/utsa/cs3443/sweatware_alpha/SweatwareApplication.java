package edu.utsa.cs3443.sweatware_alpha;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main entry point for the SweatWare application
 * <p>
 *     This class initializes the JavaFx Runtime, loads the login view from FXML, and displays the primary stage.
 * </p>
 * @author Kade Garza and Aiden Gravett
 * @version final
 */

public class SweatwareApplication extends Application {

    /**
     * Starts the JavaFX application by loading the login view and
     * displaying it in the primary stage.
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws IOException if the FXML file cannot be found or loaded
     */

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SweatwareApplication.class.getResource("login-view.fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("FXML file not found: login-view.fxml");
        }

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 440, 956);
        stage.setTitle("Sweatware");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the Sweatware application.
     * @param args command-line arguments passed to the application
     */

    public static void main(String[] args) {
        launch(args);
    }
}