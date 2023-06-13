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
    public Label timerp1 = (Label) mainCont.lookup("#timer1");
    public Label timerp2 = (Label) mainCont.lookup("#timer2");
    public Label infop1 = (Label) mainCont.lookup("#infop1");
    public Label infop2 = (Label) mainCont.lookup("#infop2");

    @Override
    public void run() {
        while (isGameON) {

            Platform.runLater(() -> handleInfo(capturesWhite, capturesBlack));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (checkWin()) {
                Platform.runLater(this::displayWinner);
                Platform.runLater(() -> handleInfo(capturesWhite, capturesBlack));
                isGameON = false;
            }
        }
    }

    public boolean checkWin() {
        if (capturesWhite == 12) whiteWon = true;
        if (capturesBlack == 12) blackWon = true;

        return whiteWon || blackWon;
    }

    public AnchorPane getMainContainer() {
        Scene currentScene = getPrimaryStage().getScene();
        Node container = currentScene.lookup("#gamescene");
        return (AnchorPane) container;
    }

    public void handleInfo(int p1, int p2) {
        String waitTurn = "Waiting for turn..";
        String moveTurn = "Your turn!";

        if (isWhiteTurn && isGameON) {
            turn1.setVisible(true);
            infop1.setText(moveTurn);
            turn2.setVisible(false);
            infop2.setText(waitTurn);
        }
        else if (!isWhiteTurn && isGameON) {
            turn1.setVisible(false);
            infop1.setText(waitTurn);
            turn2.setVisible(true);
            infop2.setText(moveTurn);
        }

        countp1.setText(String.valueOf(p1));
        countp2.setText(String.valueOf(p2));

        String time = String.format("%02d:%02d", whiteSecondsLeft / 60, whiteSecondsLeft % 60);
        timerp1.setText(time);
        time = String.format("%02d:%02d", blackSecondsLeft / 60, blackSecondsLeft % 60);
        timerp2.setText(time);
    }

    public void displayWinner() {
        String win = "You won!";
        String lose = "You lost!";

        if (whiteWon) {
            infop1.setText(win);
            infop2.setText(lose);
        }
        if (blackWon) {
            infop1.setText(lose);
            infop2.setText(win);
        }
    }
}
