package com.yu_minchen.colorsudoku.gui;

import android.os.Bundle;
import android.os.SystemClock;

import com.yu_minchen.colorsudoku.game.AbstractCommand;
import com.yu_minchen.colorsudoku.game.Cell;
import com.yu_minchen.colorsudoku.game.CellCollection;
import com.yu_minchen.colorsudoku.game.CommandStack;
import com.yu_minchen.colorsudoku.game.SetCellValueCommand;

public class SudokuGame {

    public static final int GAME_STATE_PLAYING = 0;
    public static final int GAME_STATE_NOT_STARTED = 1;
    public static final int GAME_STATE_COMPLETED = 2;

    private long mId;
    private long mCreated;
    private int mState;
    private long mTime;
    private long mLastPlayed;
    private CellCollection mCells;

    private OnPuzzleSolvedListener mOnPuzzleSolvedListener;
    private CommandStack mCommandStack;
    private long mActiveFromTime = -1;

    public SudokuGame() {
        mTime = 0;
        mLastPlayed = 0;
        mCreated = 0;
        mState = GAME_STATE_NOT_STARTED;
    }

    public void saveState(Bundle outState) {
        outState.putLong("id", mId);
        outState.putLong("created", mCreated);
        outState.putInt("state", mState);
        outState.putLong("time", mTime);
        outState.putLong("lastPlayed", mLastPlayed);
        outState.putString("cells", mCells.serialize());
        outState.putString("command_stack", mCommandStack.serialize());
    }

    public void restoreState(Bundle inState) {
        mId = inState.getLong("id");
        mCreated = inState.getLong("created");
        mState = inState.getInt("state");
        mTime = inState.getLong("time");
        mLastPlayed = inState.getLong("lastPlayed");
        mCells = CellCollection.deserialize(inState.getString("cells"));
        mCommandStack = CommandStack.deserialize(inState.getString("command_stack"), mCells);

        validate();
    }

    public void setOnPuzzleSolvedListener(OnPuzzleSolvedListener l) {
        mOnPuzzleSolvedListener = l;
    }

    public void setCreated(long created) {
        mCreated = created;
    }

    public long getCreated() {
        return mCreated;
    }

    public void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    // Sets time of play in milliseconds.
    public void setTime(long time) {
        mTime = time;
    }

    // Gets time of game-play in milliseconds.
    public long getTime() {
        if (mActiveFromTime != -1) {
            return mTime + SystemClock.uptimeMillis() - mActiveFromTime;
        } else {
            return mTime;
        }
    }

    public void setLastPlayed(long lastPlayed) {
        mLastPlayed = lastPlayed;
    }

    public long getLastPlayed() {
        return mLastPlayed;
    }

    public void setCells(CellCollection cells) {
        mCells = cells;
        validate();
        mCommandStack = new CommandStack(mCells);
    }

    public CellCollection getCells() {
        return mCells;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setCommandStack(CommandStack commandStack) {
        mCommandStack = commandStack;
    }

    public CommandStack getCommandStack() {
        return mCommandStack;
    }

    // Sets value for the given cell. 0 means empty cell.
    public void setCellValue(Cell cell, int value) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell cannot be null.");
        }
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be between 0-9.");
        }

        if (cell.isEditable()) {
            executeCommand(new SetCellValueCommand(cell, value));

            validate();
            if (isCompleted()) {
                finish();
                if (mOnPuzzleSolvedListener != null) {
                    mOnPuzzleSolvedListener.onPuzzleSolved();
                }
            }
        }
    }

    private void executeCommand(AbstractCommand c) {
        mCommandStack.execute(c);
    }

    // Start game-play.
    public void start() {
        mState = GAME_STATE_PLAYING;
        resume();
    }

    public void resume() {
        // reset time we have spent playing so far, so time when activity was not active
        // will not be part of the game play time
        mActiveFromTime = SystemClock.uptimeMillis();
    }

    // Pauses game-play (for example if activity pauses).
    public void pause() {
        // save time we have spent playing so far - it will be reseted after resuming
        mTime += SystemClock.uptimeMillis() - mActiveFromTime;
        mActiveFromTime = -1;

        setLastPlayed(System.currentTimeMillis());
    }

    // Finishes game-play. Called when puzzle is solved.
    private void finish() {
        pause();
        mState = GAME_STATE_COMPLETED;
    }

    // Resets game.
    public void reset() {
        for (int r = 0; r < CellCollection.SUDOKU_SIZE; r++) {
            for (int c = 0; c < CellCollection.SUDOKU_SIZE; c++) {
                Cell cell = mCells.getCell(r, c);
                if (cell.isEditable()) {
                    cell.setValue(0);
                    //m cell.setNote(new CellNote());
                }
            }
        }
        mCommandStack = new CommandStack(mCells);
        validate();
        setTime(0);
        setLastPlayed(0);
        mState = GAME_STATE_NOT_STARTED;
    }

    //  Returns true, if puzzle is solved. In order to know the current state, you have to call validate first.
    public boolean isCompleted() {
        return mCells.isCompleted();
    }

    // Fills in possible values which can be entered in each cell.
    public void validate() {
        mCells.validate();
    }

    // Occurs when puzzle is solved.
    public interface OnPuzzleSolvedListener {
        void onPuzzleSolved();
    }

}
