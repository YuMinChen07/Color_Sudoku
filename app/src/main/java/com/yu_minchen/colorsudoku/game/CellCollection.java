package com.yu_minchen.colorsudoku.game;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CellCollection {

    public static final int SUDOKU_SIZE = 9;

    public static int DATA_VERSION_1 = 1;
    public static int DATA_VERSION = DATA_VERSION_1;

    // Cell's data.
    private Cell[][] mCells;

    // Helper arrays, contains references to the groups of cells, which should contain unique
    private CellGroup[] mSectors;
    private CellGroup[] mRows;
    private CellGroup[] mColumns;

    private boolean mOnChangeEnabled = true;

    private final List<OnChangeListener> mChangeListeners = new ArrayList<OnChangeListener>();

    // Creates empty sudoku.
    public static CellCollection createEmpty() {
        Cell[][] cells = new Cell[SUDOKU_SIZE][SUDOKU_SIZE];

        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                cells[r][c] = new Cell();
            }
        }

        return new CellCollection(cells);
    }

    // Return true, if no value is entered in any of cells.
    public boolean isEmpty() {
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                Cell cell = mCells[r][c];
                if (cell.getValue() != 0)
                    return false;
            }
        }
        return true;
    }

    public Cell[][] getCells() {
        return mCells;
    }

    // Wraps given array in this object.
    private CellCollection(Cell[][] cells) {

        mCells = cells;
        initCollection();
    }

    // Gets cell at given position.
    public Cell getCell(int rowIndex, int colIndex) {
        return mCells[rowIndex][colIndex];
    }

    public Cell findFirstCell(int val) {
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                Cell cell = mCells[r][c];
                if (cell.getValue() == val)
                    return cell;
            }
        }
        return null;
    }


    public void markAllCellsAsValid() {
        mOnChangeEnabled = false;
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                mCells[r][c].setValid(true);
            }
        }
        mOnChangeEnabled = true;
        onChange();
    }

    // Validates numbers in collection according to the sudoku rules. Cells with invalid
    public boolean validate() {

        boolean valid = true;

        // first set all cells as valid
        markAllCellsAsValid();

        mOnChangeEnabled = false;
        for (CellGroup row : mRows) {
            if (!row.validate()) {
                valid = false;
            }
        }
        for (CellGroup column : mColumns) {
            if (!column.validate()) {
                valid = false;
            }
        }
        for (CellGroup sector : mSectors) {
            if (!sector.validate()) {
                valid = false;
            }
        }

        mOnChangeEnabled = true;
        onChange();

        return valid;
    }

    public boolean isCompleted() {
        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                Cell cell = mCells[r][c];
                if (cell.getValue() == 0 || !cell.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Initializes collection, initialization has two steps:
    private void initCollection() {
        mRows = new CellGroup[SUDOKU_SIZE];
        mColumns = new CellGroup[SUDOKU_SIZE];
        mSectors = new CellGroup[SUDOKU_SIZE];

        for (int i = 0; i < SUDOKU_SIZE; i++) {
            mRows[i] = new CellGroup();
            mColumns[i] = new CellGroup();
            mSectors[i] = new CellGroup();
        }

        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                Cell cell = mCells[r][c];

                cell.initCollection(this, r, c,
                        mSectors[((c / 3) * 3) + (r / 3)],
                        mRows[c],
                        mColumns[r]
                );
            }
        }
    }

    // Creates instance from given <code>StringTokenizer</code>
    public static CellCollection deserialize(StringTokenizer data, int version ) {
        Cell[][] cells = new Cell[SUDOKU_SIZE][SUDOKU_SIZE];

        int r = 0, c = 0;
        while (data.hasMoreTokens() && r < 9) {
            cells[r][c] = Cell.deserialize(data, version);
            c++;

            if (c == 9) {
                r++;
                c = 0;
            }
        }

        return new CellCollection(cells);
    }

    // Creates instance from given string (string which has been
    public static CellCollection deserialize(String data) {
        String[] lines = data.split("\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Cannot deserialize Sudoku, data corrupted.");
        }

        String line = lines[0];
        if (line.startsWith("version:")) {
            String[] kv = line.split(":");
            int version = Integer.parseInt(kv[1].trim());
            StringTokenizer st = new StringTokenizer(lines[1], "|");
            return deserialize(st, version);
        } else {
            return fromString(data);
        }
    }

    // Creates collection instance from given string. String is expected
    public static CellCollection fromString(String data) {
        Cell[][] cells = new Cell[SUDOKU_SIZE][SUDOKU_SIZE];

        int pos = 0;
        for (int r = 0; r < CellCollection.SUDOKU_SIZE; r++) {
            for (int c = 0; c < CellCollection.SUDOKU_SIZE; c++) {
                int value = 0;
                while (pos < data.length()) {
                    pos++;
                    if (data.charAt(pos - 1) >= '0' && data.charAt(pos - 1) <= '9') {
                        value = data.charAt(pos - 1) - '0';
                        break;
                    }
                }
                Cell cell = new Cell();
                cell.setValue(value);
                cell.setEditable(value == 0);
                cells[r][c] = cell;
            }
        }

        return new CellCollection(cells);
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        serialize(sb);
        return sb.toString();
    }

    // Writes collection to given StringBuilder. You can later recreate the object instance
    public void serialize(StringBuilder data) {
        data.append("version: ");
        data.append(DATA_VERSION);
        data.append("\n");

        for (int r = 0; r < SUDOKU_SIZE; r++) {
            for (int c = 0; c < SUDOKU_SIZE; c++) {
                Cell cell = mCells[r][c];
                cell.serialize(data);
            }
        }
    }

    public void addOnChangeListener(OnChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("The listener is null.");
        }
        synchronized (mChangeListeners) {
            if (mChangeListeners.contains(listener)) {
                throw new IllegalStateException("Listener " + listener + "is already registered.");
            }
            mChangeListeners.add(listener);
        }
    }

    // Notify all registered listeners that something has changed.
    protected void onChange() {
        if (mOnChangeEnabled) {
            synchronized (mChangeListeners) {
                for (OnChangeListener l : mChangeListeners) {
                    l.onChange();
                }
            }
        }
    }

    // Called when anything in the collection changes (cell's value, note, etc.)
    public interface OnChangeListener {
        void onChange();
    }
}
