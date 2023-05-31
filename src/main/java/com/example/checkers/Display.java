package com.example.checkers;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static com.example.checkers.Board.SIZE;
import static com.example.checkers.Board.TILE_SIZE;
import static com.example.checkers.Checkers.getPrimaryStage;

public class Display {

    private static Rectangle createSquare(boolean white) {
        Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);
        if (white) {
            square.setFill(Color.WHITE);
            square.setStroke(Color.BLACK);
        }
        else {
            square.setFill(Color.DARKRED);
            square.setStroke(Color.BLACK);
        }
        return square;
    }
    public static void printBoard(){
        Scene currentScene = getPrimaryStage().getScene();
        String containerId = "gamescene";
        Node container = currentScene.lookup("#" + containerId);

        if (container instanceof AnchorPane) {
            AnchorPane myContainer = (AnchorPane) container;

            GridPane myBoard = new GridPane();
            myBoard.setId("myboard");

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Rectangle square;
                    if ((col + 2) % 2 == 0 && row % 2 == 0 || (col + 1) % 2 == 0 && row % 2 == 1) {
                        square = createSquare(true);
                    } else {
                        square = createSquare(false);
                    }
                    myBoard.add(square, col, row);
                }
            }
            myContainer.getChildren().add(myBoard);
        }
    }
}
