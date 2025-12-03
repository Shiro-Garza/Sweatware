package edu.utsa.cs3443.sweatware_alpha;

import javafx.application.Application;

/**
 * Launcher class for the Sweatware application.
 * <p>
 * Provides the main entry point when running the application
 * outside of an IDE or build tool. Delegates to
 * {@link SweatwareApplication}, which initializes the JavaFX runtime.
 * </p>
 * <p>This class exists primarily to ensure compatibility with
 * certain build environments that require a separate launcher
 * from the {@link Application} subclass.</p>
 * @author Kade Garza and Aiden Gravett
 * @version final
 */
public class Launcher {

    /**
     * Main method that launches the Sweatware application.
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        Application.launch(SweatwareApplication.class, args);
    }
}