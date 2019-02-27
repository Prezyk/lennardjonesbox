import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class MainClass extends Application {
    Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        String fxmlDocPath = "/home/kacper/IdeaProjects/lennardjonesbox/src/main/resources/layout.fxml";

        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        AnchorPane root = loader.load(fxmlStream);

        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("A simple FXML Example");

        stage.show();

    }

    public Stage getStage() {
        return this.stage;
    }
}
