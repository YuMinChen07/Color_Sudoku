package com.yu_minchen.colorsudoku.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yu_minchen.colorsudoku.game.Cell;
import com.yu_minchen.colorsudoku.gui.SudokuBoardView.OnCellSelectedListener;
import com.yu_minchen.colorsudoku.gui.SudokuBoardView.OnCellTappedListener;

public class ControlPanel extends LinearLayout {

    private Context mContext;
    private SudokuBoardView mBoard;
    private SudokuGame mGame;

    private Numpad np;
    private int mActiveMethodIndex = -1;

    public ControlPanel(Context context) {
        super(context);
        mContext = context;
    }

    public ControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initialize(SudokuBoardView board, SudokuGame game) {
        mBoard = board;
        mBoard.setOnCellTappedListener(mOnCellTapListener);
        mBoard.setOnCellSelectedListener(mOnCellSelected);

        mGame = game;

        np = new Numpad();
        np.initialize(mContext, this, mGame, mBoard);
        ensureControlPanel();
    }

    public Numpad getInputMethod() {
        return np;
    }

    public int getActiveMethodIndex() {
        return mActiveMethodIndex;
    }

    public void pause() {
        np.pause();
    }

    // Ensures that control panel for given input method is created.
    public void ensureControlPanel(/*int methodID*/) {
        if (!np.isInputMethodViewCreated()) {
            View controlPanel = np.getInputMethodView();
            this.addView(controlPanel, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }
    }

    private OnCellTappedListener mOnCellTapListener = new OnCellTappedListener() {
        @Override
        public void onCellTapped(Cell cell) {
            np.onCellTapped(cell);
        }
    };

    private OnCellSelectedListener mOnCellSelected = new OnCellSelectedListener() {
        @Override
        public void onCellSelected(Cell cell) {
            np.onCellSelected(cell);
        }
    };
}