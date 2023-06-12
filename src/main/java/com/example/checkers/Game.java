package com.example.checkers;

import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.example.checkers.Board.*;
import static com.example.checkers.Checkers.getPrimaryStage;

public class Game {
    private Circle selectedCircle = null;
    public static boolean isWhiteTurn = true;
    public static int capturesBlack = 0;
    public static int capturesWhite = 0;
    public Game() {
        printBoard();
        printPawns();

        Runnable gameloop = new GameLoop();
        Thread thread = new Thread(gameloop);
        thread.start();
    }



    public Circle getSelectedCircle() {
        return selectedCircle;
    }

    public void setSelectedCircle(Circle selectedCircle) {
        this.selectedCircle = selectedCircle;
    }

    /*
    1. Sprawdz czy pole jest juz zaznaczone
    2. Jesli nie jest to pobierz pole i zakoncz metode
    3. Jesli zaznaczone, przemiesc pionek na wybrane pole
     */

    //Sprawdzenie czyja tura i czy klika w swoj pionek
    public boolean pawnIsValid(Circle pawn, int col, int row) {
        return (isWhiteTurn && ((Circle) arrayPawns[col][row]).getFill() == Color.WHITE) ||
                (!isWhiteTurn && ((Circle) arrayPawns[col][row]).getFill() == Color.BLACK);
    }
    public void handleCellClick(Rectangle rectangle, int col, int row) {
        //Klikniecie z pionka na pionek
        if (getSelectedCircle()!=null && arrayPawns[col][row]!=null)
        {
            setSelectedCircle(null);
            resetFieldColors();
        }

        //Pierwsze klikniecie
        if (getSelectedCircle()==null && arrayPawns[col][row]!=null && pawnIsValid((Circle) arrayPawns[col][row], col, row)) {
            setSelectedCircle((Circle) arrayPawns[col][row]);

            int moves = setPossibleMoves((Circle) arrayPawns[col][row], col, row);
            if (moves == 0) setSelectedCircle(null);
        }
        //Drugie klikniecie
        else {
            if (rectangle.getFill() == Color.LIGHTBLUE)
            {
                movePawn(getSelectedCircle(), col, row);
                isWhiteTurn = !isWhiteTurn;
            } else if (rectangle.getFill() == Color.INDIANRED) {
                capturePawn(getSelectedCircle(), col, row);

                if (isWhiteTurn) {
                    capturesWhite++;
                } else {
                    capturesBlack++;
                }
                if (checkCaptures()==0)
                    isWhiteTurn = !isWhiteTurn;
            }
            setSelectedCircle(null);
            resetFieldColors();
        }
    }

    public void movePawn(Circle pawn, int col, int row) {
        GridPane myBoard = getBoard();
        myBoard.getChildren().remove(pawn);
        myBoard.add(pawn, col, row);

        Point2D p = findCoords(pawn);
        int currentcol = (int) p.getX();
        int currentrow = (int) p.getY();

        Node temp = arrayPawns[currentcol][currentrow];
        arrayPawns[currentcol][currentrow] = null;
        arrayPawns[col][row] = temp;
    }

    public void capturePawn(Circle pawn, int col, int row) {
        GridPane myBoard = getBoard();
        Point2D p = findCoords(pawn);
        int currentcol = (int) p.getX();
        int currentrow = (int) p.getY();

        movePawn(pawn, col, row);

        Circle capturedPawn = null;
        if (col>currentcol && row>currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol+1][currentrow+1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol+1][currentrow+1] = null;

        } else if (col<currentcol && row>currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol-1][currentrow+1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol-1][currentrow+1] = null;

        } else if (col>currentcol && row<currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol+1][currentrow-1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol+1][currentrow-1] = null;

        } else if (col<currentcol && row<currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol-1][currentrow-1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol-1][currentrow-1] = null;
        }
    }

    public boolean isCapturePossibleCheck(Circle pawn, int col, int row) {
        boolean captures = false;
        if (pawn.getFill() == Color.BLACK) {
            if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == Color.WHITE && arrayPawns[col-2][row+2] == null) {
                captures = true;
            }
            if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == Color.WHITE && arrayPawns[col+2][row+2] == null) {
                captures = true;
            }
            if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == Color.WHITE && arrayPawns[col-2][row-2] == null) {
                captures = true;
            }
            if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == Color.WHITE && arrayPawns[col+2][row-2] == null) {
                captures = true;
            }
        }

        if (pawn.getFill() == Color.WHITE) {
            if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == Color.BLACK && arrayPawns[col-2][row-2] == null) {
                captures = true;
            }
            if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == Color.BLACK && arrayPawns[col+2][row-2] == null) {
                captures = true;
            }
            if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == Color.BLACK && arrayPawns[col-2][row+2] == null) {
                captures = true;
            }
            if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == Color.BLACK && arrayPawns[col+2][row+2] == null) {
                captures = true;
            }
        }

        return captures;
    }

    public boolean setCapturePossible(Circle pawn, int col, int row) {
        boolean captures = false;
        if (pawn.getFill() == Color.BLACK) {
            if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == Color.WHITE &&
                    arrayPawns[col-2][row+2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col-1][row+1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col-2][row+2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == Color.WHITE && arrayPawns[col+2][row+2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col+1][row+1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col+2][row+2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == Color.WHITE && arrayPawns[col-2][row-2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col-1][row-1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col-2][row-2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == Color.WHITE && arrayPawns[col+2][row-2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col+1][row-1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col+2][row-2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
        }

        if (pawn.getFill() == Color.WHITE) {
            if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == Color.BLACK && arrayPawns[col-2][row-2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col-1][row-1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col-2][row-2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == Color.BLACK && arrayPawns[col+2][row-2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col+1][row-1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col+2][row-2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == Color.BLACK &&
                    arrayPawns[col-2][row+2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col-1][row+1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col-2][row+2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
            if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == Color.BLACK && arrayPawns[col+2][row+2] == null) {
                Rectangle rectangle = (Rectangle) arrayFields[col+1][row+1];
                rectangle.setFill(Color.RED);
                rectangle = (Rectangle) arrayFields[col+2][row+2];
                rectangle.setFill(Color.INDIANRED);
                captures = true;
            }
        }

        return captures;
    }


    public int checkCaptures() {
        int moves = 0;
        Circle pawn;
        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col < SIZE; col++) {
                if (arrayPawns[col][row]==null) continue;
                pawn = (Circle) arrayPawns[col][row];
                if (isWhiteTurn && pawn.getFill() == Color.WHITE && isCapturePossibleCheck(pawn, col, row)) moves++;
                if (!isWhiteTurn && pawn.getFill() == Color.BLACK && isCapturePossibleCheck(pawn, col, row)) moves++;
            }
        }
        return moves;
    }
    public void resetFieldColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rectangle;
                if ((col + 2) % 2 == 0 && row % 2 == 0 || (col + 1) % 2 == 0 && row % 2 == 1) {
                    rectangle = (Rectangle) arrayFields[col][row];
                    rectangle.setFill(Color.WHITE);
                } else {
                    rectangle = (Rectangle) arrayFields[col][row];
                    rectangle.setFill(Color.DARKRED);
                }
            }
        }
    }

    public int setPossibleMoves(Circle pawn, int col, int row) {

        if (checkCaptures()==0) {

            int moves = 0;
            if (pawn.getFill() == Color.BLACK) {
                if (col > 0 && row < SIZE - 1 && arrayPawns[col - 1][row + 1] == null) {
                    Rectangle rectangle = (Rectangle) arrayFields[col - 1][row + 1];
                    rectangle.setFill(Color.LIGHTBLUE);
                    moves++;
                }
                if (col < SIZE - 1 && row < SIZE - 1 && arrayPawns[col + 1][row + 1] == null) {
                    Rectangle rectangle = (Rectangle) arrayFields[col + 1][row + 1];
                    rectangle.setFill(Color.LIGHTBLUE);
                    moves++;
                }
            }

            if (pawn.getFill() == Color.WHITE) {
                if (col > 0 && row > 0 && arrayPawns[col - 1][row - 1] == null) {
                    Rectangle rectangle = (Rectangle) arrayFields[col - 1][row - 1];
                    rectangle.setFill(Color.LIGHTBLUE);
                    moves++;
                }
                if (col < SIZE - 1 && row > 0 && arrayPawns[col + 1][row - 1] == null) {
                    Rectangle rectangle = (Rectangle) arrayFields[col + 1][row - 1];
                    rectangle.setFill(Color.LIGHTBLUE);
                    moves++;
                }
            }
            return moves;
        } else {
            setCapturePossible(pawn, col, row);
            return 1;
        }
    }

    public Point2D findCoords(Circle pawn) {
        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col < SIZE; col++) {
                if (arrayPawns[col][row]==null) continue;
                if (arrayPawns[col][row].equals((Node)pawn))
                {
                    return new Point2D(col, row);
                }
            }
        }
        return null;
    }

    private Rectangle createSquare(boolean white) {
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

            int clickedRow = GridPane.getRowIndex(square);
            int clickedCol = GridPane.getColumnIndex(square);
            handleCellClick(square, clickedCol, clickedRow);
            System.out.println("Kliknięto w pole [" + clickedRow + ", " + clickedCol + "]");
        });
        return square;
    }

    private Circle createCircle(boolean white) {
        Circle circle = new Circle(PAWN_SIZE);
        if (white) {
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
        }
        else {
            circle.setFill(Color.BLACK);
            circle.setStroke(Color.BLACK);
        }

        circle.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            int clickedRow = GridPane.getRowIndex(circle);
            int clickedCol = GridPane.getColumnIndex(circle);

            Rectangle rectangle = (Rectangle) arrayFields[clickedCol][clickedRow];
            handleCellClick(rectangle, clickedCol, clickedRow);

//            GridPane myBoard = getBoard();
//            myBoard.getChildren().remove(circle);
//            myBoard.add(circle, clickedCol+1, clickedRow-1);

            System.out.println("Kliknięto w pole [" + clickedRow + ", " + clickedCol + "]");
        });
        return circle;
    }

    public GridPane getBoard()
    {
        Scene currentScene = getPrimaryStage().getScene();
        Node container = currentScene.lookup("#gamescene");
        Node grid = container.lookup("#myboard");

        return (GridPane) grid;
    }
    public void printBoard(){
        Scene currentScene = getPrimaryStage().getScene();
        String containerId = "gamescene";
        Node container = currentScene.lookup("#" + containerId);

        if (container instanceof AnchorPane) {
            AnchorPane myContainer = (AnchorPane) container;

            GridPane myBoard = new GridPane();
            myBoard.setId("myboard");
            myBoard.setAlignment(Pos.CENTER);

            int x = ((800 - 640) / 2)-5;
            int y = (900 - 640) / 2;
            myBoard.setLayoutX(x);
            myBoard.setLayoutY(y);

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Rectangle square;
                    if ((col + 2) % 2 == 0 && row % 2 == 0 || (col + 1) % 2 == 0 && row % 2 == 1) {
                        square = createSquare(true);
                    } else {
                        square = createSquare(false);
                    }
                    arrayFields[col][row] = (Node) square;
                    myBoard.add(square, col, row);
                }
            }
            myContainer.getChildren().add(myBoard);
        }
    }


    public void printPawns(){
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
                                arrayPawns[col][row] = (Node) circle;
                                GridPane.setHalignment(circle, HPos.CENTER);
                            }
                            if (row == 5 || row == 6 || row == 7)
                            {
                                circle = createCircle(true);
                                myBoard.add(circle, col, row);
                                arrayPawns[col][row] = (Node) circle;
                                GridPane.setHalignment(circle, HPos.CENTER);
                            }
                        }
                    }
                }
//                myContainer.getChildren().add(myBoard);
            }
        }
    }
}
