package com.example.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.checkers.Display.printBoard;

public class Checkers extends Application {
    private static Stage primaryStage;
    public static Stage getPrimaryStage() {
        return Checkers.primaryStage;
    }
    private static void setPrimaryStage(Stage stage) {
        Checkers.primaryStage = stage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        setPrimaryStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(Checkers.class.getResource("game-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        printBoard();
    }

    public static void main(String[] args) {
        launch();
    }
}