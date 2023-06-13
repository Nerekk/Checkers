package com.example.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;

import static com.example.checkers.CheckersController.*;


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
        stage.setResizable(false);
//        FXMLLoader fxmlLoader = new FXMLLoader(Checkers.class.getResource("game-scene.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Checkers Game");
        setScene(OPTIONSCENE);
        stage.show();
//        Game game = new Game();
        primaryStage.setOnCloseRequest(event -> {
            closeApp();
        });
    }
    private void closeApp() {
        System.out.println("Zamykanie aplikacji...");
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}