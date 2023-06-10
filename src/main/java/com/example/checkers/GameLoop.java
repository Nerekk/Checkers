package com.example.checkers;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import static com.example.checkers.Checkers.getPrimaryStage;
import static com.example.checkers.Game.isWhiteTurn;

public class GameLoop implements Runnable{
    public AnchorPane mainCont = getMainContainer();
    public ImageView turn1 = (ImageView) mainCont.lookup("#turnp1");
    public ImageView turn2 = (ImageView) mainCont.lookup("#turnp2");
    @Override
    public void run() {
        while (true) {
            if (isWhiteTurn) {
                turn1.setVisible(true);
                turn2.setVisible(false);
            }
            else {
                turn1.setVisible(false);
                turn2.setVisible(true);
            }
        }
    }

    public AnchorPane getMainContainer() {
        Scene currentScene = getPrimaryStage().getScene();
        Node container = currentScene.lookup("#gamescene");
        return (AnchorPane) container;
    }
}
