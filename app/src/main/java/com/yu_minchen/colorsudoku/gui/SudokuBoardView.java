package com.yu_minchen.colorsudoku.gui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.yu_minchen.colorsudoku.R;
import com.yu_minchen.colorsudoku.game.Cell;
import com.yu_minchen.colorsudoku.game.CellCollection;
import com.yu_minchen.colorsudoku.game.CellCollection.OnChangeListener;

/**
 * Sudoku board widget.
 */
public class SudokuBoardView extends View {

    public static final int DEFAULT_BOARD_SIZE = 100;

    /**
     * "Color not set" value. (In relation to {@link Color}, it is in fact black color with
     * alpha channel set to 0 => that means it is completely transparent).
     */
    private static final int NO_COLOR = 0;

    private float mCellWidth;
    private float mCellHeight;

    private Cell mTouchedCell;
    private Cell mSelectedCell;
    private boolean mReadonly = false;
    private boolean mHighlightWrongVals = true;
    private boolean mHighlightTouchedCell = true;
    private boolean mAutoHideTouchedCellHint = true;
    private boolean mHighlightSimilarCells = true;

    private SudokuGame mGame;
    private CellCollection mCells;

    private OnCellTappedListener mOnCellTappedListener;
    private OnCellSelectedListener mOnCellSelectedListener;

    private Paint mLinePaint;
    private Paint mSectorLinePaint;
    private Paint mCellValuePaint;
    private Paint mCellValueReadonlyPaint;
    private Paint mCellNotePaint;
    private int mNumberLeft;
    private int mNumberTop;
    private int mSectorLineWidth;
    private Paint mBackgroundColorSecondary;
    private Paint mBackgroundColorReadOnly;
    private Paint mBackgroundColorTouched;
    private Paint mBackgroundColorSelected;
    private Paint mBackgroundColorHighlighted;
    private Paint num1Color, num2Color, num3Color, num4Color, num5Color, num6Color, num7Color, num8Color, num9Color, num0Color;

    private Paint mCellValueInvalidPaint;

    public SudokuBoardView(Context context) {
        this(context, null);
    }

    public SudokuBoardView(Context context, AttributeSet attrs/*, int defStyle*/) {
        super(context, attrs/*, defStyle*/);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mLinePaint = new Paint();
        mSectorLinePaint = new Paint();
        mCellValuePaint = new Paint();
        mCellValueReadonlyPaint = new Paint();
        mCellValueInvalidPaint = new Paint();
        mCellNotePaint = new Paint();
        mBackgroundColorSecondary = new Paint();
        mBackgroundColorReadOnly = new Paint();
        mBackgroundColorTouched = new Paint();
        mBackgroundColorSelected = new Paint();
        mBackgroundColorHighlighted = new Paint();
        num1Color = new Paint();
        num2Color = new Paint();
        num3Color = new Paint();
        num4Color = new Paint();
        num5Color = new Paint();
        num6Color = new Paint();
        num7Color = new Paint();
        num8Color = new Paint();
        num9Color = new Paint();
        num0Color = new Paint();

        mCellValuePaint.setAntiAlias(true);
        mCellValueReadonlyPaint.setAntiAlias(true);
        mCellValueInvalidPaint.setAntiAlias(true);
        mCellNotePaint.setAntiAlias(true);
        mCellValueInvalidPaint.setColor(Color.RED);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SudokuBoardView/*, defStyle, 0*/);

        setLineColor(a.getColor(R.styleable.SudokuBoardView_lineColor, Color.BLACK));
        setSectorLineColor(a.getColor(R.styleable.SudokuBoardView_sectorLineColor, Color.BLACK));
        setTextColor(a.getColor(R.styleable.SudokuBoardView_textColor, Color.BLACK));
        setTextColorReadOnly(a.getColor(R.styleable.SudokuBoardView_textColorReadOnly, Color.BLACK));
        setTextColorNote(a.getColor(R.styleable.SudokuBoardView_textColorNote, Color.BLACK));
        setBackgroundColor(a.getColor(R.styleable.SudokuBoardView_backgroundColor, Color.WHITE));
        setBackgroundColorSecondary(a.getColor(R.styleable.SudokuBoardView_backgroundColorSecondary, NO_COLOR));
        setBackgroundColorReadOnly(a.getColor(R.styleable.SudokuBoardView_backgroundColorReadOnly, NO_COLOR));
        setBackgroundColorTouched(a.getColor(R.styleable.SudokuBoardView_backgroundColorTouched, Color.rgb(50, 50, 255)));
        setBackgroundColorSelected(a.getColor(R.styleable.SudokuBoardView_backgroundColorSelected, Color.YELLOW));
        setBackgroundColorHighlighted(a.getColor(R.styleable.SudokuBoardView_backgroundColorHighlighted, Color.GREEN));
        setNum1Color(a.getColor(R.styleable.SudokuBoardView_num1, Color.parseColor("#09ff00")));
        setNum2Color(a.getColor(R.styleable.SudokuBoardView_num2, Color.parseColor("#e3ff00")));
        setNum3Color(a.getColor(R.styleable.SudokuBoardView_num3,Color.parseColor("#ffca07")));
        setNum4Color(a.getColor(R.styleable.SudokuBoardView_num4, Color.parseColor("#e87d0c")));
        setNum5Color(a.getColor(R.styleable.SudokuBoardView_num5, Color.parseColor("#ff0082")));
        setNum6Color(a.getColor(R.styleable.SudokuBoardView_num6, Color.parseColor("#9900ff")));
        setNum7Color(a.getColor(R.styleable.SudokuBoardView_num7, Color.parseColor("#0114ff")));
        setNum8Color(a.getColor(R.styleable.SudokuBoardView_num8, Color.parseColor("#0ca8e8")));
        setNum9Color(a.getColor(R.styleable.SudokuBoardView_num9, Color.parseColor("#00ff9d")));
        //setNum0Color(a.getColor(R.styleable.SudokuBoardView_backgroundColorSecondary, NO_COLOR));

        a.recycle();
    }
    public void setNum1Color(int color) {
        num1Color.setColor(color);
    }

    public void setNum2Color(int color) {
        num2Color.setColor(color);
    }

    public void setNum3Color(int color) {
        num3Color.setColor(color);
    }

    public void setNum4Color(int color) {
        num4Color.setColor(color);
    }

    public void setNum5Color(int color) {
        num5Color.setColor(color);
    }

    public void setNum6Color(int color) {
        num6Color.setColor(color);
    }

    public void setNum7Color(int color) {
        num7Color.setColor(color);
    }

    public void setNum8Color(int color) {
        num8Color.setColor(color);
    }

    public void setNum9Color(int color) {
        num9Color.setColor(color);
    }

    public void setNum0Color(int color) {
        num4Color.setColor(color);
    }

    public int getLineColor() {
        return mLinePaint.getColor();
    }

    public void setLineColor(int color) {
        mLinePaint.setColor(color);
    }

    public int getSectorLineColor() {
        return mSectorLinePaint.getColor();
    }

    public void setSectorLineColor(int color) {
        mSectorLinePaint.setColor(color);
    }

    public int getTextColor() {
        return mCellValuePaint.getColor();
    }

    public void setTextColor(int color) {
        mCellValuePaint.setColor(color);
    }

    public int getTextColorReadOnly() {
        return mCellValueReadonlyPaint.getColor();
    }

    public void setTextColorReadOnly(int color) {
        mCellValueReadonlyPaint.setColor(color);
    }

    public int getTextColorNote() {
        return mCellNotePaint.getColor();
    }

    public void setTextColorNote(int color) {
        mCellNotePaint.setColor(color);
    }

    public int getBackgroundColorSecondary() {
        return mBackgroundColorSecondary.getColor();
    }

    public void setBackgroundColorSecondary(int color) {
        mBackgroundColorSecondary.setColor(color);
    }

    public int getBackgroundColorReadOnly() {
        return mBackgroundColorReadOnly.getColor();
    }

    public void setBackgroundColorReadOnly(int color) {
        mBackgroundColorReadOnly.setColor(color);
    }

    public int getBackgroundColorTouched() {
        return mBackgroundColorTouched.getColor();
    }

    public void setBackgroundColorTouched(int color) {
        mBackgroundColorTouched.setColor(color);
    }

    public int getBackgroundColorSelected() {
        return mBackgroundColorSelected.getColor();
    }

    public void setBackgroundColorSelected(int color) {
        mBackgroundColorSelected.setColor(color);
    }

    public int getBackgroundColorHighlighted() {
        return mBackgroundColorHighlighted.getColor();
    }

    public void setBackgroundColorHighlighted(int color) {
        mBackgroundColorHighlighted.setColor(color);
    }

    public void setGame(SudokuGame game) {
        mGame = game;
        setCells(game.getCells());
    }

    public void setCells(CellCollection cells) {
        mCells = cells;

        if (mCells != null) {
            if (!mReadonly) {
                mSelectedCell = mCells.getCell(0, 0); // first cell will be selected by default
                onCellSelected(mSelectedCell);
            }

            mCells.addOnChangeListener(new OnChangeListener() {
                @Override
                public void onChange() {
                    postInvalidate();
                }
            });
        }

        postInvalidate();
    }

    public CellCollection getCells() {
        return mCells;
    }

    public Cell getSelectedCell() {
        return mSelectedCell;
    }

    public void setReadOnly(boolean readonly) {
        mReadonly = readonly;
        postInvalidate();
    }

    public boolean isReadOnly() {
        return mReadonly;
    }

    public void setHighlightWrongVals(boolean highlightWrongVals) {
        mHighlightWrongVals = highlightWrongVals;
        postInvalidate();
    }

    public boolean getHighlightWrongVals() {
        return mHighlightWrongVals;
    }

    public void setHighlightTouchedCell(boolean highlightTouchedCell) {
        mHighlightTouchedCell = highlightTouchedCell;
    }

    public boolean getHighlightTouchedCell() {
        return mHighlightTouchedCell;
    }

    public void setAutoHideTouchedCellHint(boolean autoHideTouchedCellHint) {
        mAutoHideTouchedCellHint = autoHideTouchedCellHint;
    }

    public boolean getAutoHideTouchedCellHint() {
        return mAutoHideTouchedCellHint;
    }

    public void setHighlightSimilarCell(boolean highlightSimilarCell) {
        mHighlightSimilarCells = highlightSimilarCell;
    }

    public boolean getHighlightSimilarCell() {
        return mHighlightSimilarCells;
    }

    /**
     * Registers callback which will be invoked when user taps the cell.
     */
    public void setOnCellTappedListener(OnCellTappedListener l) {
        mOnCellTappedListener = l;
    }

    protected void onCellTapped(Cell cell) {
        if (mOnCellTappedListener != null) {
            mOnCellTappedListener.onCellTapped(cell);
        }
    }

    /**
     * Registers callback which will be invoked when cell is selected. Cell selection
     * can change without user interaction.
     *
     * @param l
     */
    public void setOnCellSelectedListener(OnCellSelectedListener l) {
        mOnCellSelectedListener = l;
    }

    public void hideTouchedCellHint() {
        mTouchedCell = null;
        postInvalidate();
    }


    protected void onCellSelected(Cell cell) {
        if (mOnCellSelectedListener != null) {
            mOnCellSelectedListener.onCellSelected(cell);
        }
    }

    public void invokeOnCellSelected() {
        onCellSelected(mSelectedCell);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = -1, height = -1;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = DEFAULT_BOARD_SIZE;
            if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
                width = widthSize;
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = DEFAULT_BOARD_SIZE;
            if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
                height = heightSize;
            }
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
            height = heightSize;
        }

        mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 9.0f;
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 9.0f;

        setMeasuredDimension(width, height);

        float cellTextSize = mCellHeight * 0.75f;
        mCellValuePaint.setTextSize(cellTextSize);
        mCellValueReadonlyPaint.setTextSize(cellTextSize);
        mCellValueInvalidPaint.setTextSize(cellTextSize);
        mCellNotePaint.setTextSize(mCellHeight / 3.0f);
        // compute offsets in each cell to center the rendered number
        mNumberLeft = (int) ((mCellWidth - mCellValuePaint.measureText("9")) / 2);
        mNumberTop = (int) ((mCellHeight - mCellValuePaint.getTextSize()) / 2);

        // add some offset because in some resolutions notes are cut-off in the top
        //m mNoteTop = mCellHeight / 50.0f;

        computeSectorLineWidth(width, height);
    }

    private void computeSectorLineWidth(int widthInPx, int heightInPx) {
        int sizeInPx = widthInPx < heightInPx ? widthInPx : heightInPx;
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        float sizeInDip = sizeInPx / dipScale;

        float sectorLineWidthInDip = 2.0f;

        if (sizeInDip > 150) {
            sectorLineWidthInDip = 3.0f;
        }

        mSectorLineWidth = (int) (sectorLineWidthInDip * dipScale);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // some notes:
        // Drawable has its own draw() method that takes your Canvas as an arguement

        // TODO: I don't get this, why do I need to substract padding only from one side?
        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        // draw secondary background
        if (mBackgroundColorSecondary.getColor() != NO_COLOR) {
            canvas.drawRect(3 * mCellWidth, 0, 6 * mCellWidth, 3 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(0, 3 * mCellWidth, 3 * mCellWidth, 6 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(6 * mCellWidth, 3 * mCellWidth, 9 * mCellWidth, 6 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(3 * mCellWidth, 6 * mCellWidth, 6 * mCellWidth, 9 * mCellWidth, mBackgroundColorSecondary);
        }

        // draw cells
        int cellLeft, cellTop;
        if (mCells != null) {

            boolean hasBackgroundColorReadOnly = mBackgroundColorReadOnly.getColor() != NO_COLOR;

            float numberAscent = mCellValuePaint.ascent();
            float noteAscent = mCellNotePaint.ascent();
            float noteWidth = mCellWidth / 3f;

            int selectedValue = 0;
            if (mHighlightSimilarCells && mSelectedCell != null) {
                selectedValue = mSelectedCell.getValue();
            }

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Cell cell = mCells.getCell(row, col);

                    cellLeft = Math.round((col * mCellWidth) + paddingLeft);
                    cellTop = Math.round((row * mCellHeight) + paddingTop);

                    // draw read-only field background
                    if (!cell.isEditable() && hasBackgroundColorReadOnly &&
                            (mSelectedCell == null || mSelectedCell != cell)) {
                        if (mBackgroundColorReadOnly.getColor() != NO_COLOR) {
                            canvas.drawRect(
                                    cellLeft, cellTop,
                                    cellLeft + mCellWidth, cellTop + mCellHeight,
                                    mBackgroundColorReadOnly);
                        }
                    }

                    // highlight similar cells
                    if (selectedValue != 0 && selectedValue == cell.getValue() &&
                            (mSelectedCell == null || mSelectedCell != cell)) {
                        if (mBackgroundColorHighlighted.getColor() != NO_COLOR) {
                            canvas.drawRect(
                                    cellLeft, cellTop,
                                    cellLeft + mCellWidth, cellTop + mCellHeight,
                                    mBackgroundColorHighlighted);
                        }
                    }
                }
            }

            // highlight selected cell
            if (!mReadonly && mSelectedCell != null) {
                cellLeft = Math.round(mSelectedCell.getColumnIndex() * mCellWidth) + paddingLeft;
                cellTop = Math.round(mSelectedCell.getRowIndex() * mCellHeight) + paddingTop;
                canvas.drawRect(
                        cellLeft, cellTop,
                        cellLeft + mCellWidth, cellTop + mCellHeight,
                        mBackgroundColorSelected);
            }

            // visually highlight cell under the finger (to cope with touch screen
            // imprecision)
            if (mHighlightTouchedCell && mTouchedCell != null) {
                cellLeft = Math.round(mTouchedCell.getColumnIndex() * mCellWidth) + paddingLeft;
                cellTop = Math.round(mTouchedCell.getRowIndex() * mCellHeight) + paddingTop;
                canvas.drawRect(
                        cellLeft, paddingTop,
                        cellLeft + mCellWidth, height,
                        mBackgroundColorTouched);
                canvas.drawRect(
                        paddingLeft, cellTop,
                        width, cellTop + mCellHeight,
                        mBackgroundColorTouched);
            }

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Cell cell = mCells.getCell(row, col);

                    cellLeft = Math.round((col * mCellWidth) + paddingLeft);
                    cellTop = Math.round((row * mCellHeight) + paddingTop);

                    // draw cell Text
                    int value = cell.getValue();
                    if (value != 0) {

                        Paint cellValuePaint;
                        switch(value) {
                            case 1 :
                                cellValuePaint = num1Color;
                                break;
                            case 2 :
                                cellValuePaint = num2Color;
                                break;
                            case 3 :
                                cellValuePaint = num3Color;
                                break;
                            case 4 :
                                cellValuePaint = num4Color;
                                break;
                            case 5 :
                                cellValuePaint = num5Color;
                                break;
                            case 6 :
                                cellValuePaint = num6Color;
                                break;
                            case 7 :
                                cellValuePaint = num7Color;
                                break;
                            case 8 :
                                cellValuePaint = num8Color;
                                break;
                            case 9 :
                                cellValuePaint = num9Color;
                                break;
                            default:
                                cellValuePaint = mCellValueReadonlyPaint;
                        }
                        canvas.drawText(Integer.toString(value),
                                cellLeft + mNumberLeft,
                                cellTop + mNumberTop - numberAscent,
                                cellValuePaint);
                        canvas.drawRoundRect(
                                cellLeft, cellTop,
                                cellLeft + mCellWidth, cellTop + mCellHeight, mCellWidth, mCellHeight,
                                cellValuePaint);
                    }
                }
            }
        }

        // draw vertical lines
        for (int c = 0; c <= 9; c++) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawLine(x, paddingTop, x, height, mLinePaint);
        }

        // draw horizontal lines
        for (int r = 0; r <= 9; r++) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
        }

        int sectorLineWidth1 = mSectorLineWidth / 2;
        int sectorLineWidth2 = sectorLineWidth1 + (mSectorLineWidth % 2);

        // draw sector (thick) lines
        for (int c = 0; c <= 9; c = c + 3) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawRect(x - sectorLineWidth1, paddingTop, x + sectorLineWidth2, height, mSectorLinePaint);
        }

        for (int r = 0; r <= 9; r = r + 3) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawRect(paddingLeft, y - sectorLineWidth1, width, y + sectorLineWidth2, mSectorLinePaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mReadonly) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mTouchedCell = getCellAtPoint(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    mSelectedCell = getCellAtPoint(x, y);
                    invalidate(); // selected cell has changed, update board as soon as you can

                    if (mSelectedCell != null) {
                        onCellTapped(mSelectedCell);
                        onCellSelected(mSelectedCell);
                    }

                    if (mAutoHideTouchedCellHint) {
                        mTouchedCell = null;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mTouchedCell = null;
                    break;
            }
            postInvalidate();
        }

        return !mReadonly;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mReadonly) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    return moveCellSelection(0, -1);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return moveCellSelection(1, 0);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return moveCellSelection(0, 1);
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return moveCellSelection(-1, 0);
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_DEL:
                    // clear value in selected cell
                    // TODO: I'm not really sure that this is thread-safe
                    if (mSelectedCell != null) {
                        if (event.isShiftPressed() || event.isAltPressed()) {
                            //m setCellNote(mSelectedCell, CellNote.EMPTY);
                        } else {
                            setCellValue(mSelectedCell, 0);
                            moveCellSelectionRight();
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mSelectedCell != null) {
                        onCellTapped(mSelectedCell);
                    }
                    return true;
            }

            if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
                int selNumber = keyCode - KeyEvent.KEYCODE_0;
                Cell cell = mSelectedCell;

                setCellValue(cell, selNumber);
                moveCellSelectionRight();

                return true;
            }
        }


        return false;
    }


    /**
     * Moves selected cell by one cell to the right. If edge is reached, selection
     * skips on beginning of another line.
     */
    public void moveCellSelectionRight() {
        if (!moveCellSelection(1, 0)) {
            int selRow = mSelectedCell.getRowIndex();
            selRow++;
            if (!moveCellSelectionTo(selRow, 0)) {
                moveCellSelectionTo(0, 0);
            }
        }
        postInvalidate();
    }

    private void setCellValue(Cell cell, int value) {
        if (cell.isEditable()) {
            if (mGame != null) {
                mGame.setCellValue(cell, value);
            } else {
                cell.setValue(value);
            }
        }
    }

    /**
     * Moves selected by vx cells right and vy cells down. vx and vy can be negative. Returns true,
     * if new cell is selected.
     *
     * @param vx Horizontal offset, by which move selected cell.
     * @param vy Vertical offset, by which move selected cell.
     */
    private boolean moveCellSelection(int vx, int vy) {
        int newRow = 0;
        int newCol = 0;

        if (mSelectedCell != null) {
            newRow = mSelectedCell.getRowIndex() + vy;
            newCol = mSelectedCell.getColumnIndex() + vx;
        }

        return moveCellSelectionTo(newRow, newCol);
    }


    /**
     * Moves selection to the cell given by row and column index.
     *
     * @param row Row index of cell which should be selected.
     * @param col Columnd index of cell which should be selected.
     * @return True, if cell was successfuly selected.
     */
    public boolean moveCellSelectionTo(int row, int col) {
        if (col >= 0 && col < CellCollection.SUDOKU_SIZE
                && row >= 0 && row < CellCollection.SUDOKU_SIZE) {
            mSelectedCell = mCells.getCell(row, col);
            onCellSelected(mSelectedCell);

            postInvalidate();
            return true;
        }

        return false;
    }

    /**
     * Returns cell at given screen coordinates. Returns null if no cell is found.
     *
     * @param x
     * @param y
     * @return
     */
    private Cell getCellAtPoint(int x, int y) {
        // take into account padding
        int lx = x - getPaddingLeft();
        int ly = y - getPaddingTop();

        int row = (int) (ly / mCellHeight);
        int col = (int) (lx / mCellWidth);

        if (col >= 0 && col < CellCollection.SUDOKU_SIZE
                && row >= 0 && row < CellCollection.SUDOKU_SIZE) {
            return mCells.getCell(row, col);
        } else {
            return null;
        }
    }

    /**
     * Occurs when user tap the cell.
     */
    public interface OnCellTappedListener {
        void onCellTapped(Cell cell);
    }

    /**
     * Occurs when user selects the cell.
     */
    public interface OnCellSelectedListener {
        void onCellSelected(Cell cell);
    }

}
