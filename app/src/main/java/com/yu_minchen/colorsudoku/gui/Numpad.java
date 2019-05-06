package com.yu_minchen.colorsudoku.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.yu_minchen.colorsudoku.R;
import com.yu_minchen.colorsudoku.game.Cell;
import com.yu_minchen.colorsudoku.game.CellCollection;
import com.yu_minchen.colorsudoku.game.CellCollection.OnChangeListener;
import com.yu_minchen.colorsudoku.gui.ControlPanelPersister.StateBundle;

import java.util.HashMap;
import java.util.Map;

public class Numpad extends InputMethod {

    private boolean moveCellSelectionOnPress = true;
    private boolean mHighlightCompletedValues = true;
    private boolean mShowNumberTotals = false;

    private static final int MODE_EDIT_VALUE = 0;
    //private static final int MODE_EDIT_NOTE = 1;

    private Cell mSelectedCell;
    //private ImageButton mSwitchNumNoteButton;

    private int mEditMode = MODE_EDIT_VALUE;

    private Map<Integer, Button> mNumberButtons;

    public boolean isMoveCellSelectionOnPress() {
        return moveCellSelectionOnPress;
    }

    public void setMoveCellSelectionOnPress(boolean moveCellSelectionOnPress) {
        this.moveCellSelectionOnPress = moveCellSelectionOnPress;
    }

    public boolean getHighlightCompletedValues() {
        return mHighlightCompletedValues;
    }

    /**
     * If set to true, buttons for numbers, which occur in {@link CellCollection}
     * more than {@link CellCollection#SUDOKU_SIZE}-times, will be highlighted.
     *
     * @param highlightCompletedValues
     */
    public void setHighlightCompletedValues(boolean highlightCompletedValues) {
        mHighlightCompletedValues = highlightCompletedValues;
    }

    public boolean getShowNumberTotals() {
        return mShowNumberTotals;
    }

    public void setShowNumberTotals(boolean showNumberTotals) {
        mShowNumberTotals = showNumberTotals;
    }

    @Override
    protected void initialize(Context context, ControlPanel controlPanel,
                              SudokuGame game, SudokuBoardView board) {
        super.initialize(context, controlPanel, game, board);
        game.getCells().addOnChangeListener(mOnCellsChangeListener);
    }

    @Override
    protected View createControlPanelView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View controlPanel = inflater.inflate(R.layout.numpad, null);

        mNumberButtons = new HashMap<Integer, Button>();
        mNumberButtons.put(1, (Button) controlPanel.findViewById(R.id.button_1));
        mNumberButtons.put(2, (Button) controlPanel.findViewById(R.id.button_2));
        mNumberButtons.put(3, (Button) controlPanel.findViewById(R.id.button_3));
        mNumberButtons.put(4, (Button) controlPanel.findViewById(R.id.button_4));
        mNumberButtons.put(5, (Button) controlPanel.findViewById(R.id.button_5));
        mNumberButtons.put(6, (Button) controlPanel.findViewById(R.id.button_6));
        mNumberButtons.put(7, (Button) controlPanel.findViewById(R.id.button_7));
        mNumberButtons.put(8, (Button) controlPanel.findViewById(R.id.button_8));
        mNumberButtons.put(9, (Button) controlPanel.findViewById(R.id.button_9));
        mNumberButtons.put(0, (Button) controlPanel.findViewById(R.id.button_clear));

         for (Integer num : mNumberButtons.keySet()) {
            Button b = mNumberButtons.get(num);
            b.setTag(num);
            b.setOnClickListener(mNumberButtonClick);
        }

        update();

        return controlPanel;
    }

    @Override
    protected void onActivated() {
        update();

        mSelectedCell = mBoard.getSelectedCell();
    }

    @Override
    protected void onCellSelected(Cell cell) {
        mSelectedCell = cell;
    }

    private View.OnClickListener mNumberButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int selNumber = (Integer) v.getTag();
            Cell selCell = mSelectedCell;

            if (selCell != null) {
                switch (mEditMode) {
                    case MODE_EDIT_VALUE:
                        if (selNumber >= 0 && selNumber <= 9) {
                            mGame.setCellValue(selCell, selNumber);
                            if (isMoveCellSelectionOnPress()) {
                                mBoard.moveCellSelectionRight();
                            }
                        }
                        break;
                }
            }
        }

    };

    private OnChangeListener mOnCellsChangeListener = new OnChangeListener() {

        @Override
        public void onChange() {
            if (mActive) {
                update();
            }
        }
    };


    private void update() {
        /*Map<Integer, Integer> valuesUseCount = null;
        if (mHighlightCompletedValues || mShowNumberTotals)
            valuesUseCount = mGame.getCells().getValuesUseCount();

        if (mHighlightCompletedValues) {
            for (Map.Entry<Integer, Integer> entry : valuesUseCount.entrySet()) {
                boolean highlightValue = entry.getValue() >= CellCollection.SUDOKU_SIZE;
                Button b = mNumberButtons.get(entry.getKey());
                if (highlightValue) {
                    //b.getBackground().setColorFilter(0xFF1B5E20, PorterDuff.Mode.MULTIPLY);
                    b.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                } else {
                    b.getBackground().setColorFilter(null);
                }
            }
        }*/
    }

    @Override
    protected void onSaveState(StateBundle outState) {
        outState.putInt("editMode", mEditMode);
    }

    @Override
    protected void onRestoreState(StateBundle savedState) {
        mEditMode = savedState.getInt("editMode", MODE_EDIT_VALUE);
        if (isInputMethodViewCreated()) {
            update();
        }
    }
}
