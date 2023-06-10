package com.example.checkers;

import javafx.scene.Node;

public class Board {
    char[][] map;
    public static final int SIZE = 8;
    public static final int TILE_SIZE = 80;
    public static final int PAWN_SIZE = 30;
    public static final char BLACK_PAWN = 'b';
    public static final char WHITE_PAWN = 'w';
    public static Node[][] arrayFields = new Node[SIZE][SIZE];
    public static Node[][] arrayPawns = new Node[SIZE][SIZE];

    public Board() {
        this.map = new char[SIZE][SIZE];
    }

}
