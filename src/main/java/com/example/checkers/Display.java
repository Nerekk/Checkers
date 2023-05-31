package com.example.checkers;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static com.example.checkers.Board.*;
import static com.example.checkers.Checkers.getPrimaryStage;

public class Display {

    private static Rectangle selectedCell;

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

        square.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            handleCellClick(square);
            int clickedRow = GridPane.getRowIndex(square);
            int clickedCol = GridPane.getColumnIndex(square);
            System.out.println("Kliknięto w pole [" + clickedRow + ", " + clickedCol + "]");
        });
        return square;
    }

    private static Circle createCircle(boolean white) {
        Circle circle = new Circle(PAWN_SIZE);
        if (white) {
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
        }
        else {
            circle.setFill(Color.BLACK);
            circle.setStroke(Color.BLACK);
        }
        return circle;
    }
    public static void printBoard(){
        Scene currentScene = getPrimaryStage().getScene();
        String containerId = "gamescene";
        Node container = currentScene.lookup("#" + containerId);

        if (container instanceof AnchorPane) {
            AnchorPane myContainer = (AnchorPane) container;

            GridPane myBoard = new GridPane();
            myBoard.setId("myboard");
            myBoard.setAlignment(Pos.CENTER);

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

    public static void printPawns(){
        Scene currentScene = getPrimaryStage().getScene();
        String containerId = "gamescene";
        Node container = currentScene.lookup("#" + containerId);

        if (container instanceof AnchorPane) {
            AnchorPane myContainer = (AnchorPane) container;

            Node gridContainer = currentScene.lookup("#myboard");
            if (gridContainer instanceof GridPane)
            {
                GridPane myBoard = (GridPane) gridContainer;

                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {
                        Circle circle;
                        if ((col + 2) % 2 == 0 && row % 2 == 0 || (col + 1) % 2 == 0 && row % 2 == 1) {
                            circle = createCircle(true);
                        } else {
                            if (row == 0 || row == 1 || row == 2)
                            {
                                circle = createCircle(false);
                                myBoard.add(circle, col, row);
                                GridPane.setHalignment(circle, HPos.CENTER);
                            }
                            if (row == 5 || row == 6 || row == 7)
                            {
                                circle = createCircle(true);
                                myBoard.add(circle, col, row);
                                GridPane.setHalignment(circle, HPos.CENTER);
                            }
                        }
                    }
                }
//                myContainer.getChildren().add(myBoard);
            }
        }
    }

    private static void handleCellClick(Rectangle cell) {
        if (selectedCell == null) {
            // Jeśli żaden pionek nie jest jeszcze zaznaczony, zaznacz bieżący pionek
            selectedCell = cell;
        } else {
            // Jeśli już jest zaznaczony pionek, przenieś go na nową pozycję
            int selectedRow = GridPane.getRowIndex(selectedCell);
            int selectedCol = GridPane.getColumnIndex(selectedCell);
            int newRow = GridPane.getRowIndex(cell);
            int newCol = GridPane.getColumnIndex(cell);

            // Usuń zaznaczenie z poprzedniego pola
            selectedCell.setStroke(Color.BLACK);
            selectedCell = null;

            // Przenieś pionek na nowe pole
            GridPane.setRowIndex(cell, selectedRow);
            GridPane.setColumnIndex(cell, selectedCol);
        }
    }
}
