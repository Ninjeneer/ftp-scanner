package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("gui/scanner/main.fxml")));
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("Scanner");
        stage.setScene(scene);
        stage.show();
    }

    public void start() {
        Application.launch();
    }
}
