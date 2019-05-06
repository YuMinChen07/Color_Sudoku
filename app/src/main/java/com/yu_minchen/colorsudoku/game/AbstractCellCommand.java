package com.yu_minchen.colorsudoku.game;

public abstract class AbstractCellCommand extends AbstractCommand {

    private CellCollection mCells;

    protected CellCollection getCells() {
        return mCells;
    }

    protected void setCells(CellCollection mCells) {
        this.mCells = mCells;
    }

}

