package com.example.checkers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;

import static com.example.checkers.Checkers.getPrimaryStage;
import static com.example.checkers.Game.enemyType;
import static com.example.checkers.Game.isServer;

public class CheckersController {
    public static String GAMESCENE = "game-scene.fxml";
    public static String OPTIONSCENE = "options-scene.fxml";
    public static void setScene(String sceneFXML) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Checkers.class.getResource(sceneFXML));
            getPrimaryStage().setScene(new Scene(fxmlLoader.load()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void prepareGameServer() {
        setScene(GAMESCENE);
        Game game = new Game(true, 1);
    }

    @FXML
    public void prepareGameClient() {
        setScene(GAMESCENE);
        Game game = new Game(false, 1);
    }

    @FXML
    public void prepareGameLocal() {
        setScene(GAMESCENE);
        Game game = new Game(false, 0);
    }


}