package edu.utsa.cs3443.sweatware_alpha;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SweatwareApplication extends Application {
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

    public static void main(String[] args) {
        launch(args);
    }
}