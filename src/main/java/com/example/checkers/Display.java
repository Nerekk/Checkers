package com.example.checkers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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




//    private static void handleCellClick(Rectangle cell) {
//        if (selectedCell == null) {
//            // Jeśli żaden pionek nie jest jeszcze zaznaczony, zaznacz bieżący pionek
//            selectedCell = cell;
//        } else {
//            // Jeśli już jest zaznaczony pionek, przenieś go na nową pozycję
//            int selectedRow = GridPane.getRowIndex(selectedCell);
//            int selectedCol = GridPane.getColumnIndex(selectedCell);
//            int newRow = GridPane.getRowIndex(cell);
//            int newCol = GridPane.getColumnIndex(cell);
//
//            // Usuń zaznaczenie z poprzedniego pola
//            selectedCell.setStroke(Color.BLACK);
//            selectedCell = null;
//
//            // Przenieś pionek na nowe pole
//            GridPane.setRowIndex(cell, selectedRow);
//            GridPane.setColumnIndex(cell, selectedCol);
//        }
//    }
}
