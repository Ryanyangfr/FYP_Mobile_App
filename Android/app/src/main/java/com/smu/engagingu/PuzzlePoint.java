package com.smu.engagingu;

import java.io.Serializable;

public class PuzzlePoint implements Serializable {
    private int row;
    private int column;

    public PuzzlePoint(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
