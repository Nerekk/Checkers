package com.example.checkers;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class Board {
    char[][] map;
    public static final int SIZE = 8;
    public static final int TILE_SIZE = 80;
    public static final int PAWN_SIZE = 30;
    public static final Color LIGHT_TILE_COLOR = Color.WHITE;
    public static final Color DARK_TILE_COLOR = Color.DARKRED;
    public static final Color BLACK_PAWN_COLOR = Color.BLACK;
    public static final Color WHITE_PAWN_COLOR = Color.WHITE;
    public static final Color KING_STROKE_COLOR = Color.YELLOW;
    public static final int KING_STROKE_WIDTH = 40;
    public static Node[][] arrayFields = new Node[SIZE][SIZE];
    public static Node[][] arrayPawns = new Node[SIZE][SIZE];

    public Board() {
        this.map = new char[SIZE][SIZE];
    }

}
