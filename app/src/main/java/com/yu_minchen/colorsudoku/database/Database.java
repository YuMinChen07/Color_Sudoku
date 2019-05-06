package com.yu_minchen.colorsudoku.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.yu_minchen.colorsudoku.game.CellCollection;
import com.yu_minchen.colorsudoku.game.CommandStack;
import com.yu_minchen.colorsudoku.gui.SudokuGame;

public class Database {
    public static final String DATABASE_NAME = "colorsudoku";
    public static final String SUDOKU_TABLE_NAME = "sudoku";
    public static final String FOLDER_TABLE_NAME = "folder";

    private DatabaseHelper mOpenHelper;

    public Database(Context context) {
        mOpenHelper = new DatabaseHelper(context);
    }

    // Returns sudoku game object.
    public SudokuGame getSudoku(long sudokuID) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Log.v(String.valueOf(sudokuID), "check");

        qb.setTables(SUDOKU_TABLE_NAME);
        qb.appendWhere(SudokuColumns._ID + "=" + sudokuID);

        // Get the database and run the query

        SQLiteDatabase db;
        Cursor c = null;
        SudokuGame s = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);

            if (c.moveToFirst()) {
                long id = c.getLong(c.getColumnIndex(SudokuColumns._ID));
                long created = c.getLong(c.getColumnIndex(SudokuColumns.CREATED));
                String data = c.getString(c.getColumnIndex(SudokuColumns.DATA));
                long lastPlayed = c.getLong(c.getColumnIndex(SudokuColumns.LAST_PLAYED));
                int state = c.getInt(c.getColumnIndex(SudokuColumns.STATE));
                long time = c.getLong(c.getColumnIndex(SudokuColumns.TIME));

                s = new SudokuGame();
                s.setId(id);
                s.setCreated(created);
                s.setCells(CellCollection.deserialize(data));
                s.setLastPlayed(lastPlayed);
                s.setState(state);
                s.setTime(time);

                if (s.getState() == SudokuGame.GAME_STATE_PLAYING) {
                    String command_stack =  c.getString(c.getColumnIndex(SudokuColumns.COMMAND_STACK));
                    if (command_stack != null  && command_stack != "") {
                        s.setCommandStack(CommandStack.deserialize(command_stack, s.getCells()));
                    }
                }
            }
        } finally {
            if (c != null) c.close();
        }

        return s;
    }


    // Updates sudoku game in the database.
    public void updateSudoku(SudokuGame sudoku) {
        ContentValues values = new ContentValues();
        values.put(SudokuColumns.DATA, sudoku.getCells().serialize());
        values.put(SudokuColumns.LAST_PLAYED, sudoku.getLastPlayed());
        values.put(SudokuColumns.STATE, sudoku.getState());
        values.put(SudokuColumns.TIME, sudoku.getTime());
        String command_stack = null;
        if (sudoku.getState() == SudokuGame.GAME_STATE_PLAYING) {
            command_stack =  sudoku.getCommandStack().serialize();
        }
        values.put(SudokuColumns.COMMAND_STACK, command_stack);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.update(SUDOKU_TABLE_NAME, values, SudokuColumns._ID + "=" + sudoku.getId(), null);
    }

    public void close() {
        mOpenHelper.close();
    }
}
