package ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class of the JavaFX application.
 * This is where everything starts — it opens the dashboard window.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MonsterHighApp extends Application {

    /**
     * JavaFX calls this method automatically when the app starts.
     * We create the dashboard and show it.
     *
     * @param primaryStage the main window provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        DashboardView dashboard = new DashboardView(primaryStage);
        dashboard.show();
    }

    /**
     * Main method — launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
