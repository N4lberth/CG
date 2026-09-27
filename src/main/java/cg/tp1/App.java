package cg.tp1;

import cg.tp1.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Ponto de entrada JavaFX: apenas cria a janela principal.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        new MainWindow(stage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
