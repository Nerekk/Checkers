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

import java.util.Timer;
import java.util.TimerTask;

import static com.example.checkers.Board.*;
import static com.example.checkers.Checkers.getPrimaryStage;

public class Game {
    // 1.PLAYER LOCAL 2.PLAYER ONLINE 3. CPU
    public static int enemyType;
    private Circle selectedCircle = null;
    public static boolean isGameON = true;
    public static boolean whiteWon = false;
    public static boolean blackWon = false;
    public static boolean isWhiteTurn = true;
    public static int capturesBlack = 0;
    public static int capturesWhite = 0;
    public static int whiteSecondsLeft = 300;
    public static int blackSecondsLeft = 300;
    public Game() {
        printBoard();
        printPawns();

        Runnable gameloop = new GameLoop();
        Thread thread = new Thread(gameloop);
        thread.start();

        timerWhiteInit();
        timerBlackInit();

    }

    void timerWhiteInit() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (whiteSecondsLeft > 0 && isWhiteTurn) {
                    whiteSecondsLeft--;
                }
                if (whiteSecondsLeft==0) {
                    blackWon = true;
                    System.out.println("Czas bialych minął!");
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    void timerBlackInit() {
        Timer timer2 = new Timer();
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                if (blackSecondsLeft > 0 && !isWhiteTurn) {
                    blackSecondsLeft--;
                }
                if (blackSecondsLeft==0) {
                    whiteWon = true;
                    System.out.println("Czas czarnych minął!");
                    timer2.cancel();
                }
            }
        };
        timer2.scheduleAtFixedRate(task2, 0, 1000);
    }

    public Circle getSelectedCircle() {
        return selectedCircle;
    }

    public void setSelectedCircle(Circle selectedCircle) {
        this.selectedCircle = selectedCircle;
    }

    public boolean pawnIsValid(Circle pawn, int col, int row) {
        return (isWhiteTurn && ((Circle) arrayPawns[col][row]).getFill() == WHITE_PAWN_COLOR) ||
                (!isWhiteTurn && ((Circle) arrayPawns[col][row]).getFill() == BLACK_PAWN_COLOR);
    }

    public void handleCellClick(Rectangle rectangle, int col, int row) {

        handlePawnToPawnClick(col, row);

        if (isFirstClick(col, row)) {

            handleFirstClick(col, row);
        }
        else {
            handleSecondClick(rectangle, col, row);
        }
    }

    private boolean isFirstClick(int col, int row) {
        return getSelectedCircle() == null && arrayPawns[col][row] != null && pawnIsValid((Circle) arrayPawns[col][row], col, row);
    }

    private void handleSecondClick(Rectangle rectangle, int col, int row) {
        if (getSelectedCircle()!=null && getSelectedCircle().getStroke().equals(KING_STROKE_COLOR)) {
            handleMoveKing(rectangle, col, row);
        } else {
            handleMovePawn(rectangle, col, row);

        }
    }

    private void handlePawnToPawnClick(int col, int row) {
        if (getSelectedCircle()!=null && arrayPawns[col][row]!=null)
        {
            setSelectedCircle(null);
            resetFieldColors();
        }
    }

    private void handleFirstClick(int col, int row) {
        setSelectedCircle((Circle) arrayPawns[col][row]);

        int moves;
        if (((Circle) arrayPawns[col][row]).getStroke().equals(KING_STROKE_COLOR)) {
            moves = setPossibleMovesKing((Circle) arrayPawns[col][row], col, row);
        } else {
            moves = setPossibleMoves((Circle) arrayPawns[col][row], col, row);

        }
        if (moves == 0) setSelectedCircle(null);
    }

    public void handleMoveKing(Rectangle rectangle, int col, int row) {
        if (rectangle.getFill() == Color.LIGHTBLUE)
        {
            movePawn(getSelectedCircle(), col, row);
            isWhiteTurn = !isWhiteTurn;
        } else if (rectangle.getFill() == Color.INDIANRED) {
            captureKing(getSelectedCircle(), col, row);

            countCapture();
            if (checkMultipleCaptures(getSelectedCircle(), col, row)==0)
                isWhiteTurn = !isWhiteTurn;
        }
        setSelectedCircle(null);
        resetFieldColors();
    }

    private static void countCapture() {
        if (isWhiteTurn) {
            capturesWhite++;
        } else {
            capturesBlack++;
        }
    }

    public void handleMovePawn(Rectangle rectangle, int col, int row) {
        if (rectangle.getFill() == Color.LIGHTBLUE)
        {
            movePawn(getSelectedCircle(), col, row);
            isWhiteTurn = !isWhiteTurn;
        } else if (rectangle.getFill() == Color.INDIANRED) {
            capturePawn(getSelectedCircle(), col, row);

            countCapture();
            if (checkMultipleCaptures(getSelectedCircle(), col, row)==0)
                isWhiteTurn = !isWhiteTurn;
        }
        setSelectedCircle(null);
        resetFieldColors();
    }

    public void movePawn(Circle pawn, int col, int row) {
        GridPane myBoard = getBoard();
        myBoard.getChildren().remove(pawn);
        myBoard.add(pawn, col, row);

        Point2D p = findCoords(pawn);
        int currentcol = (int) p.getX();
        int currentrow = (int) p.getY();

        moveNodePawn(col, row, currentcol, currentrow);
        checkKingTransformation(pawn, row);
    }

    private static void moveNodePawn(int col, int row, int currentcol, int currentrow) {
        Node temp = arrayPawns[currentcol][currentrow];
        arrayPawns[currentcol][currentrow] = null;
        arrayPawns[col][row] = temp;
    }

    public void captureKing(Circle pawn, int col, int row) {
        GridPane myBoard = getBoard();
        Point2D p = findCoords(pawn);
        int currentcol = (int) p.getX();
        int currentrow = (int) p.getY();

        movePawn(pawn, col, row);

        removeCapturedPawnByKing(col, row, myBoard, currentcol, currentrow);
    }

    private static void removeCapturedPawnByKing(int col, int row, GridPane myBoard, int currentcol, int currentrow) {
        Circle capturedPawn;
        if (col > currentcol && row > currentrow) {
            for (int c = currentcol, r = currentrow; c< col && r< row; c++, r++)
            {
                if (arrayPawns[c][r]!=null) {
                    capturedPawn = (Circle) arrayPawns[c][r];
                    myBoard.getChildren().remove(capturedPawn);
                    arrayPawns[c][r] = null;
                    break;
                }
            }

        } else if (col < currentcol && row > currentrow) {
            for (int c = currentcol, r = currentrow; c> col && r< row; c--, r++)
            {
                if (arrayPawns[c][r]!=null) {
                    capturedPawn = (Circle) arrayPawns[c][r];
                    myBoard.getChildren().remove(capturedPawn);
                    arrayPawns[c][r] = null;
                    break;
                }
            }

        } else if (col > currentcol && row < currentrow) {
            for (int c = currentcol, r = currentrow; c< col && r> row; c++, r--)
            {
                if (arrayPawns[c][r]!=null) {
                    capturedPawn = (Circle) arrayPawns[c][r];
                    myBoard.getChildren().remove(capturedPawn);
                    arrayPawns[c][r] = null;
                    break;
                }
            }

        } else if (col < currentcol && row < currentrow) {
            for (int c = currentcol, r = currentrow; c> col && r> row; c--, r--)
            {
                if (arrayPawns[c][r]!=null) {
                    capturedPawn = (Circle) arrayPawns[c][r];
                    myBoard.getChildren().remove(capturedPawn);
                    arrayPawns[c][r] = null;
                    break;
                }
            }
        }
    }

    public void capturePawn(Circle pawn, int col, int row) {
        GridPane myBoard = getBoard();
        Point2D p = findCoords(pawn);
        int currentcol = (int) p.getX();
        int currentrow = (int) p.getY();

        movePawn(pawn, col, row);

        removeCapturedPawn(col, row, myBoard, currentcol, currentrow);
    }

    private static void removeCapturedPawn(int col, int row, GridPane myBoard, int currentcol, int currentrow) {
        Circle capturedPawn;
        if (col > currentcol && row > currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol +1][currentrow +1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol +1][currentrow +1] = null;

        } else if (col < currentcol && row > currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol -1][currentrow +1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol -1][currentrow +1] = null;

        } else if (col > currentcol && row < currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol +1][currentrow -1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol +1][currentrow -1] = null;

        } else if (col < currentcol && row < currentrow) {
            capturedPawn = (Circle) arrayPawns[currentcol -1][currentrow -1];
            myBoard.getChildren().remove(capturedPawn);
            arrayPawns[currentcol -1][currentrow -1] = null;
        }
    }

    public boolean isCapturePossibleCheckKing(Circle pawn, int col, int row) {
        boolean captures = false;
        if (pawn.getFill() == BLACK_PAWN_COLOR) {
            captures = isCapturesKing(col, row, WHITE_PAWN_COLOR, captures);

        }

        if (pawn.getFill() == WHITE_PAWN_COLOR) {
            //sprawdzenie lewo dol
            captures = isCapturesKing(col, row, BLACK_PAWN_COLOR, captures);
        }

        return captures;
    }

    private static boolean isCapturesKing(int col, int row, Color whitePawnColor, boolean captures) {
        //sprawdzenie lewo dol
        if (col > 1 && row < SIZE - 2) {
            for (int c = col, r = row; c > 1 && r < SIZE - 2; c--, r++) {
                if (arrayPawns[c - 1][r + 1] != null) {
                    if (((Circle) arrayPawns[c - 1][r + 1]).getFill() == whitePawnColor && arrayPawns[c - 2][r + 2] == null) {
                        captures = true;
                    }
                    break;
                }
            }
        }

        // prawo dol
        if (col < SIZE - 2 && row < SIZE - 2) {
            for (int c = col, r = row; c < SIZE - 2 && r < SIZE - 2; c++, r++) {
                if (arrayPawns[c + 1][r + 1] != null) {
                    if (((Circle) arrayPawns[c + 1][r + 1]).getFill() == whitePawnColor && arrayPawns[c + 2][r + 2] == null) {
                        captures = true;
                    }
                    break;
                }
            }
        }

        // lewo gora
        if (col > 1 && row > 1) {
            for (int c = col, r = row; c > 1 && r > 1; c--, r--) {
                if (arrayPawns[c - 1][r - 1] != null) {
                    if (((Circle) arrayPawns[c - 1][r - 1]).getFill() == whitePawnColor && arrayPawns[c - 2][r - 2] == null) {
                        captures = true;
                    }
                    break;
                }
            }
        }

        // prawo gora
        if (col < SIZE - 2 && row > 1) {
            for (int c = col, r = row; c < SIZE - 2 && r > 1; c++, r--) {
                if (arrayPawns[c + 1][r - 1] != null) {
                    if (((Circle) arrayPawns[c + 1][r - 1]).getFill() == whitePawnColor && arrayPawns[c + 2][r - 2] == null) {
                        captures = true;
                    }
                    break;
                }
            }
        }
        return captures;
    }

    public boolean setCapturePossibleKing(Circle pawn, int col, int row) {
        boolean captures1 = false;
        boolean captures2 = false;
        boolean captures3 = false;
        boolean captures4 = false;
        if (pawn.getFill() == BLACK_PAWN_COLOR) {
            //sprawdzenie lewo dol
            if (col > 1 && row < SIZE - 2) {
                for (int c = col, r = row; c>0 && r<SIZE-1; c--, r++) {

                    if (arrayPawns[c-1][r+1] != null && captures1) break;

                    if (arrayPawns[c-1][r+1] == null && captures1) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r+1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c>1 && r<SIZE-2 && arrayPawns[c-1][r+1] != null && ((Circle)arrayPawns[c-1][r+1]).getFill() == WHITE_PAWN_COLOR && arrayPawns[c-2][r+2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r+1];
                        rectangle.setFill(Color.RED);
                        captures1 = true;
                    }

                }
            }

            // prawo dol
            if (col < SIZE - 2 && row < SIZE - 2) {
                for (int c = col, r = row; c<SIZE-1 && r<SIZE-1; c++, r++) {

                    if (arrayPawns[c+1][r+1] != null && captures2) break;

                    if (arrayPawns[c+1][r+1] == null && captures2) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r+1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c<SIZE-2 && r<SIZE-2 && arrayPawns[c+1][r+1] != null && ((Circle)arrayPawns[c+1][r+1]).getFill() == WHITE_PAWN_COLOR && arrayPawns[c+2][r+2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r+1];
                        rectangle.setFill(Color.RED);
                        captures2 = true;
                    }


                }
            }

            // lewo gora
            if (col>1 && row>1) {
                for (int c = col, r = row; c>0 && r>0; c--, r--) {

                    if (arrayPawns[c-1][r-1] != null && captures3) break;

                    if (arrayPawns[c-1][r-1] == null && captures3) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r-1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c>1 && r>1 && arrayPawns[c-1][r-1] != null && ((Circle)arrayPawns[c-1][r-1]).getFill() == WHITE_PAWN_COLOR && arrayPawns[c-2][r-2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r-1];
                        rectangle.setFill(Color.RED);
                        captures3 = true;
                    }

                }
            }

            // prawo gora
            if (col<SIZE-2 && row>1) {
                for (int c = col, r = row; c<SIZE-1 && r>0; c++, r--) {

                    if (arrayPawns[c+1][r-1] != null && captures4) break;

                    if (arrayPawns[c+1][r-1] == null && captures4) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r-1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c<SIZE-2 && r>1 && arrayPawns[c+1][r-1] != null && ((Circle)arrayPawns[c+1][r-1]).getFill() == WHITE_PAWN_COLOR && arrayPawns[c+2][r-2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r-1];
                        rectangle.setFill(Color.RED);
                        captures4 = true;
                    }

                }
            }

        }

        if (pawn.getFill() == WHITE_PAWN_COLOR) {
            //sprawdzenie lewo dol
            if (col > 1 && row < SIZE - 2) {
                for (int c = col, r = row; c>0 && r<SIZE-1; c--, r++) {

                    if (arrayPawns[c-1][r+1] != null && captures1) break;

                    if (arrayPawns[c-1][r+1] == null && captures1) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r+1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c>1 && r<SIZE-2 && arrayPawns[c-1][r+1] != null && ((Circle)arrayPawns[c-1][r+1]).getFill() == BLACK_PAWN_COLOR && arrayPawns[c-2][r+2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r+1];
                        rectangle.setFill(Color.RED);
                        captures1 = true;
                    }

                }
            }

            // prawo dol
            if (col < SIZE - 2 && row < SIZE - 2) {
                for (int c = col, r = row; c<SIZE-1 && r<SIZE-1; c++, r++) {

                    if (arrayPawns[c+1][r+1] != null && captures2) break;

                    if (arrayPawns[c+1][r+1] == null && captures2) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r+1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c<SIZE-2 && r<SIZE-2 && arrayPawns[c+1][r+1] != null && ((Circle)arrayPawns[c+1][r+1]).getFill() == BLACK_PAWN_COLOR && arrayPawns[c+2][r+2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r+1];
                        rectangle.setFill(Color.RED);
                        captures2 = true;
                    }


                }
            }

            // lewo gora
            if (col>1 && row>1) {
                for (int c = col, r = row; c>0 && r>0; c--, r--) {

                    if (arrayPawns[c-1][r-1] != null && captures3) break;

                    if (arrayPawns[c-1][r-1] == null && captures3) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r-1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c>1 && r>1 && arrayPawns[c-1][r-1] != null && ((Circle)arrayPawns[c-1][r-1]).getFill() == BLACK_PAWN_COLOR && arrayPawns[c-2][r-2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c-1][r-1];
                        rectangle.setFill(Color.RED);
                        captures3 = true;
                    }

                }
            }

            // prawo gora
            if (col<SIZE-2 && row>1) {
                for (int c = col, r = row; c<SIZE-1 && r>0; c++, r--) {

                    if (arrayPawns[c+1][r-1] != null && captures4) break;

                    if (arrayPawns[c+1][r-1] == null && captures4) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r-1];
                        rectangle.setFill(Color.INDIANRED);
                    }

                    if (c<SIZE-2 && r>1 && arrayPawns[c+1][r-1] != null && ((Circle)arrayPawns[c+1][r-1]).getFill() == BLACK_PAWN_COLOR && arrayPawns[c+2][r-2] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c+1][r-1];
                        rectangle.setFill(Color.RED);
                        captures4 = true;
                    }

                }
            }
        }

        return captures1 || captures2 || captures3 || captures4;
    }

    public boolean isCapturePossibleCheck(Circle pawn, int col, int row) {
        boolean captures = false;
        if (pawn.getFill() == BLACK_PAWN_COLOR) {
            captures = isCapturesDown(col, row, captures, WHITE_PAWN_COLOR);
            captures = isCapturesUp(col, row, captures, WHITE_PAWN_COLOR);
        }

        if (pawn.getFill() == WHITE_PAWN_COLOR) {
            captures = isCapturesUp(col, row, captures, BLACK_PAWN_COLOR);
            captures = isCapturesDown(col, row, captures, BLACK_PAWN_COLOR);
        }

        return captures;
    }

    private boolean isCapturesDown(int col, int row, boolean captures, Color blackPawnColor) {
        if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == blackPawnColor && arrayPawns[col-2][row+2] == null) {
            captures = true;
        }
        if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == blackPawnColor && arrayPawns[col+2][row+2] == null) {
            captures = true;
        }
        return captures;
    }

    private boolean isCapturesUp(int col, int row, boolean captures, Color blackPawnColor) {
        if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == blackPawnColor && arrayPawns[col-2][row-2] == null) {
            captures = true;
        }
        if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == blackPawnColor && arrayPawns[col+2][row-2] == null) {
            captures = true;
        }
        return captures;
    }

    public boolean setCapturePossible(Circle pawn, int col, int row) {
        boolean captures = false;
        if (pawn.getFill() == BLACK_PAWN_COLOR) {
            captures = setCapturesDown(col, row, captures, WHITE_PAWN_COLOR);
            captures = setCapturesUp(col, row, captures, WHITE_PAWN_COLOR);
        }

        if (pawn.getFill() == WHITE_PAWN_COLOR) {
            captures = setCapturesUp(col, row, captures, BLACK_PAWN_COLOR);
            captures = setCapturesDown(col, row, captures, BLACK_PAWN_COLOR);
        }

        return captures;
    }

    private boolean setCapturesUp(int col, int row, boolean captures, Color whitePawnColor) {
        if (col>1 && row>1 && arrayPawns[col-1][row-1] != null && ((Circle)arrayPawns[col-1][row-1]).getFill() == whitePawnColor && arrayPawns[col-2][row-2] == null) {
            Rectangle rectangle = (Rectangle) arrayFields[col-1][row-1];
            rectangle.setFill(Color.RED);
            rectangle = (Rectangle) arrayFields[col-2][row-2];
            rectangle.setFill(Color.INDIANRED);
            captures = true;
        }
        if (col<SIZE-2 && row>1 && arrayPawns[col+1][row-1] != null && ((Circle)arrayPawns[col+1][row-1]).getFill() == whitePawnColor && arrayPawns[col+2][row-2] == null) {
            Rectangle rectangle = (Rectangle) arrayFields[col+1][row-1];
            rectangle.setFill(Color.RED);
            rectangle = (Rectangle) arrayFields[col+2][row-2];
            rectangle.setFill(Color.INDIANRED);
            captures = true;
        }
        return captures;
    }

    private boolean setCapturesDown(int col, int row, boolean captures, Color whitePawnColor) {
        if (col>1 && row<SIZE-2 && arrayPawns[col-1][row+1] != null && ((Circle)arrayPawns[col-1][row+1]).getFill() == whitePawnColor && arrayPawns[col-2][row+2] == null) {
            Rectangle rectangle = (Rectangle) arrayFields[col-1][row+1];
            rectangle.setFill(Color.RED);
            rectangle = (Rectangle) arrayFields[col-2][row+2];
            rectangle.setFill(Color.INDIANRED);
            captures = true;
        }
        if (col < SIZE - 2 && row < SIZE - 2  && arrayPawns[col+1][row+1] != null && ((Circle)arrayPawns[col+1][row+1]).getFill() == whitePawnColor && arrayPawns[col+2][row+2] == null) {
            Rectangle rectangle = (Rectangle) arrayFields[col+1][row+1];
            rectangle.setFill(Color.RED);
            rectangle = (Rectangle) arrayFields[col+2][row+2];
            rectangle.setFill(Color.INDIANRED);
            captures = true;
        }
        return captures;
    }

    public void checkKingTransformation(Circle pawn, int row) {
        if (pawn.getFill() == WHITE_PAWN_COLOR && row == 0) {
            pawn.setStroke(KING_STROKE_COLOR);
            pawn.setStrokeWidth(KING_STROKE_WIDTH);
        }

        if (pawn.getFill() == BLACK_PAWN_COLOR && row == SIZE-1) {
            pawn.setStroke(KING_STROKE_COLOR);
            pawn.setStrokeWidth(KING_STROKE_WIDTH);
        }
    }

    public int checkCaptures() {
        int moves = 0;
        Circle pawn;
        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col < SIZE; col++) {
                if (arrayPawns[col][row]==null) continue;
                pawn = (Circle) arrayPawns[col][row];
                moves = getMoves(moves, pawn, row, col);
            }
        }
        return moves;
    }

    private int getMoves(int moves, Circle pawn, int row, int col) {
        if (isWhiteTurn && pawn.getFill() == WHITE_PAWN_COLOR && isCapturePossibleCheck(pawn, col, row)) moves++;
        if (isWhiteTurn && pawn.getFill() == WHITE_PAWN_COLOR && pawn.getStroke().equals(KING_STROKE_COLOR) && isCapturePossibleCheckKing(pawn, col, row)) moves++;
        if (!isWhiteTurn && pawn.getFill() == BLACK_PAWN_COLOR && isCapturePossibleCheck(pawn, col, row)) moves++;
        if (!isWhiteTurn && pawn.getFill() == BLACK_PAWN_COLOR && pawn.getStroke().equals(KING_STROKE_COLOR) && isCapturePossibleCheckKing(pawn, col, row)) moves++;
        return moves;
    }

    public int checkMultipleCaptures(Circle pawn, int col, int row) {
        int moves = 0;
        moves = getMoves(moves, pawn, row, col);
        return moves;
    }

    public void resetFieldColors() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rectangle;
                if ((col + 2) % 2 == 0 && row % 2 == 0 || (col + 1) % 2 == 0 && row % 2 == 1) {
                    rectangle = (Rectangle) arrayFields[col][row];
                    rectangle.setFill(LIGHT_TILE_COLOR);
                } else {
                    rectangle = (Rectangle) arrayFields[col][row];
                    rectangle.setFill(DARK_TILE_COLOR);
                }
            }
        }
    }

    public int setPossibleMovesKing(Circle pawn, int col, int row) {

        if (checkCaptures()==0) {

            int moves = 0;

            if (col > 0 && row < SIZE - 1) {
                for (int c = col, r = row; c>0 && r<SIZE-1; c--, r++) {
                    if (arrayPawns[c - 1][r + 1] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c - 1][r + 1];
                        rectangle.setFill(Color.LIGHTBLUE);
                        moves++;
                    } else {
                        break;
                    }
                }

            }

            if (col < SIZE - 1 && row < SIZE - 1) {
                for (int c = col, r = row; c<SIZE-1 && r<SIZE-1; c++, r++) {
                    if (arrayPawns[c + 1][r + 1] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c + 1][r + 1];
                        rectangle.setFill(Color.LIGHTBLUE);
                        moves++;
                    } else {
                        break;
                    }
                }
            }

            if (col > 0 && row > 0) {
                for (int c = col, r = row; c>0 && r>0; c--, r--) {
                    if (arrayPawns[c - 1][r - 1] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c - 1][r - 1];
                        rectangle.setFill(Color.LIGHTBLUE);
                        moves++;
                    } else {
                        break;
                    }
                }
            }

            if (col < SIZE - 1 && row > 0) {
                for (int c = col, r = row; c<SIZE-1 && r>0; c++, r--) {
                    if (arrayPawns[c + 1][r - 1] == null) {
                        Rectangle rectangle = (Rectangle) arrayFields[c + 1][r - 1];
                        rectangle.setFill(Color.LIGHTBLUE);
                        moves++;
                    } else {
                        break;
                    }
                }
            }

            return moves;
        } else {
            setCapturePossibleKing(pawn, col, row);
            return 1;
        }
    }

    public int setPossibleMoves(Circle pawn, int col, int row) {

        if (checkCaptures()==0) {

            int moves = 0;
            if (pawn.getFill() == BLACK_PAWN_COLOR) {
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

            if (pawn.getFill() == WHITE_PAWN_COLOR) {
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
            square.setFill(LIGHT_TILE_COLOR);
            square.setStroke(Color.BLACK);
        }
        else {
            square.setFill(DARK_TILE_COLOR);
            square.setStroke(Color.BLACK);
        }

        square.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isGameON) {
                int clickedRow = GridPane.getRowIndex(square);
                int clickedCol = GridPane.getColumnIndex(square);
                handleCellClick(square, clickedCol, clickedRow);
                System.out.println("Pressed field [" + clickedCol + ", " + clickedRow + "]");
            }
        });
        return square;
    }

    private Circle createCircle(boolean white) {
        Circle circle = new Circle(PAWN_SIZE);
        if (white) {
            circle.setFill(WHITE_PAWN_COLOR);
            circle.setStroke(Color.BLACK);
        }
        else {
            circle.setFill(BLACK_PAWN_COLOR);
            circle.setStroke(Color.BLACK);
        }

        circle.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isGameON) {
                int clickedRow = GridPane.getRowIndex(circle);
                int clickedCol = GridPane.getColumnIndex(circle);

                Rectangle rectangle = (Rectangle) arrayFields[clickedCol][clickedRow];
                handleCellClick(rectangle, clickedCol, clickedRow);

                System.out.println("Pressed field [" + clickedCol + ", " + clickedRow + "]");
            }
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

        if (container instanceof AnchorPane myContainer) {

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

            Node gridContainer = currentScene.lookup("#myboard");
            if (gridContainer instanceof GridPane myBoard)
            {

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
            }
        }
    }
}
