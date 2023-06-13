package com.example.checkers;

import java.io.Serializable;

public class Packet implements Serializable {
    public int startcol;
    public int startrow;
    public int endcol;
    public int endrow;
    public boolean isKing;
    public boolean isCapture;


    public Packet(int startcol, int startrow, int endcol, int endrow, boolean isKing, boolean isCapture) {
        this.startcol = startcol;
        this.startrow = startrow;
        this.endcol = endcol;
        this.endrow = endrow;
        this.isKing = isKing;
        this.isCapture = isCapture;
    }
}
