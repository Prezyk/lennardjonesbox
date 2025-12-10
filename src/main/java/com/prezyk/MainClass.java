package com.prezyk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainClass extends Application {
    Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("layout.fxml"));

        AnchorPane root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("A simple FXML Example");

        stage.show();

    }
}
