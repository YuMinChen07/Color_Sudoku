package com.yu_minchen.colorsudoku.gui;

import android.content.Context;
import android.view.View;

import com.yu_minchen.colorsudoku.game.Cell;
import com.yu_minchen.colorsudoku.gui.ControlPanelPersister.StateBundle;

public abstract class InputMethod {

    protected Context mContext;
    protected ControlPanel mControlPanel;
    protected SudokuGame mGame;
    protected SudokuBoardView mBoard;

    private String mInputMethodName;
    protected View mInputMethodView;

    protected boolean mActive = false;
    private boolean mEnabled = true;

    public InputMethod() {

    }

    protected void initialize(Context context, ControlPanel controlPanel, SudokuGame game, SudokuBoardView board) {
        mContext = context;
        mControlPanel = controlPanel;
        mGame = game;
        mBoard = board;
        mInputMethodName = this.getClass().getSimpleName();
    }

    public boolean isInputMethodViewCreated() {
        return mInputMethodView != null;
    }

    public View getInputMethodView() {
        if (mInputMethodView == null) {
            mInputMethodView = createControlPanelView();
            onControlPanelCreated(mInputMethodView);
        }

        return mInputMethodView;
    }

    public void pause() {
        onPause();
    }

    protected void onPause() { }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void activate() {
        mActive = true;
        onActivated();
    }

    public void deactivate() {
        mActive = false;
        onDeactivated();
    }

    protected abstract View createControlPanelView();

    protected void onControlPanelCreated(View controlPanel) { }

    protected void onActivated() { }

    protected void onDeactivated() { }

    // Called when cell is selected. Please note that cell selection can
    protected void onCellSelected(Cell cell) { }

    // Called when cell is tapped.
    protected void onCellTapped(Cell cell) { }

    protected void onSaveState(StateBundle outState) { }

    protected void onRestoreState(StateBundle savedState) { }
}
