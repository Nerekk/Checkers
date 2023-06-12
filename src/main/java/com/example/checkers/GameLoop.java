package com.example.checkers;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import static com.example.checkers.Checkers.getPrimaryStage;
import static com.example.checkers.Game.*;

public class GameLoop implements Runnable{
    public AnchorPane mainCont = getMainContainer();
    public ImageView turn1 = (ImageView) mainCont.lookup("#turnp1");
    public ImageView turn2 = (ImageView) mainCont.lookup("#turnp2");
    public Label countp1 = (Label) mainCont.lookup("#countp1");
    public Label countp2 = (Label) mainCont.lookup("#countp2");
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
            Platform.runLater(() -> displayCount(capturesWhite, capturesBlack));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public AnchorPane getMainContainer() {
        Scene currentScene = getPrimaryStage().getScene();
        Node container = currentScene.lookup("#gamescene");
        return (AnchorPane) container;
    }

    public void displayCount(int p1, int p2) {
        countp1.setText(String.valueOf(p1));
        countp2.setText(String.valueOf(p2));
    }
}
