package com.yu_minchen.colorsudoku.game;

import java.util.StringTokenizer;

public class Cell {
    // if cell is included in collection, here are some additional information
    // about collection and cell's position in it
    private CellCollection mCellCollection;
    private final Object mCellCollectionLock = new Object();
    private int mRowIndex = -1;
    private int mColumnIndex = -1;
    private CellGroup mSector; // sector containing this cell
    private CellGroup mRow; // row containing this cell
    private CellGroup mColumn; // column containing this cell

    private int mValue;
    private boolean mEditable;
    private boolean mValid;

    // Creates empty editable cell.
    public Cell() {
        this(0,true, true);
    }

    // Creates empty editable cell containing given value.
    public Cell(int value) {
        this(value,true, true);
    }

    private Cell(int value, boolean editable, boolean valid) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 0-9.");
        }

        mValue = value;
        mEditable = editable;
        mValid = valid;
    }

    // Gets cell's row index within
    public int getRowIndex() {
        return mRowIndex;
    }

    //  Gets cell's column index within
    public int getColumnIndex() {
        return mColumnIndex;
    }

    protected void initCollection(CellCollection cellCollection, int rowIndex, int colIndex,
                                  CellGroup sector, CellGroup row, CellGroup column) {
        synchronized (mCellCollectionLock) {
            mCellCollection = cellCollection;
        }

        mRowIndex = rowIndex;
        mColumnIndex = colIndex;
        mSector = sector;
        mRow = row;
        mColumn = column;

        sector.addCell(this);
        row.addCell(this);
        column.addCell(this);
    }

    // Sets cell's value. Value can be 1-9 or 0 if cell should be empty.
    public void setValue(int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 0-9.");
        }
        mValue = value;
        onChange();
    }

    // Gets cell's value. Value can be 1-9 or 0 if cell is empty.
    public int getValue() {
        return mValue;
    }

    // Returns whether cell can be edited.
    public boolean isEditable() {
        return mEditable;
    }

    // Sets whether cell can be edited.
    public void setEditable(Boolean editable) {
        mEditable = editable;
        onChange();
    }

    // Sets whether cell contains valid value according to sudoku rules.
    public void setValid(Boolean valid) {
        mValid = valid;
        onChange();
    }

    //  Returns true, if cell contains valid value according to sudoku rules.
    public boolean isValid() {
        return mValid;
    }

    // Creates instance from given <code>StringTokenizer</code>.
    public static Cell deserialize(StringTokenizer data, int version) {
        Cell cell = new Cell();
        cell.setValue(Integer.parseInt(data.nextToken()));
        cell.setEditable(data.nextToken().equals("1"));

        return cell;
    }

    // Creates instance from given string (string which has been
    public static Cell deserialize(String cellData) {
        StringTokenizer data = new StringTokenizer(cellData, "|");
        return deserialize(data, CellCollection.DATA_VERSION);
    }

    // Appends string representation of this object to the given StringBuilder
    public void serialize(StringBuilder data) {
        data.append(mValue).append("|");
        data.append(mEditable ? "1" : "0").append("|");
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        serialize(sb);
        return sb.toString();
    }

    // Notify CellCollection that something has changed.
    private void onChange() {
        synchronized (mCellCollectionLock) {
            if (mCellCollection != null) {
                mCellCollection.onChange();
            }
        }
    }

}

