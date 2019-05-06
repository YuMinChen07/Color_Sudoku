/*
 *  Copyright (C) 2019 Yumin Chen
 */

package com.yu_minchen.colorsudoku.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yu_minchen.colorsudoku.AndroidUtils;
import com.yu_minchen.colorsudoku.R;
import com.yu_minchen.colorsudoku.database.Database;
import com.yu_minchen.colorsudoku.gui.SudokuGame.OnPuzzleSolvedListener;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SETTINGS = 1;

    private long mSudokuGameID;
    private SudokuGame mSudokuGame;

    private Database mDatabase;
    private Handler mGuiHandler;

    private ViewGroup mRootLayout;
    private SudokuBoardView mSudokuBoard;
    private TextView mTimeLabel;

    private ControlPanel mControlPanel;
    private ControlPanelPersister mControlPanelPersister;
    private Numpad mNumpad;

    private boolean mShowTime = true;
    private GameTimer mGameTimer;
    private TimerFormat mGameTimeFormatter = new TimerFormat();
    private boolean mFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Create","on");

        Display display = getWindowManager().getDefaultDisplay();
        if ((display.getWidth() == 240 || display.getWidth() == 320)
                && (display.getHeight() == 240 || display.getHeight() == 320)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mFullScreen = true;
        }

        AndroidUtils.setThemeFromPreferences(this);

        setContentView(R.layout.activity_main);

        mRootLayout = findViewById(R.id.root_layout);
        mSudokuBoard = findViewById(R.id.sudoku_board);
        mTimeLabel = findViewById(R.id.time_label);

        mDatabase = new Database(getApplicationContext());
        mGameTimer = new GameTimer();

        mGuiHandler = new Handler();

        // create sudoku game instance
        if (savedInstanceState == null) {
            mSudokuGameID = 1;
            mSudokuGame = mDatabase.getSudoku(mSudokuGameID);
        } else {
            // activity has been running before, restore its state
            mSudokuGame = new SudokuGame();
            mSudokuGame.restoreState(savedInstanceState);
            mGameTimer.restoreState(savedInstanceState);
        }

        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_NOT_STARTED) {
            mSudokuGame.start();
        } else if (mSudokuGame.getState() == SudokuGame.GAME_STATE_PLAYING) {
            mSudokuGame.resume();
        }

        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_COMPLETED) {
            mSudokuBoard.setReadOnly(true);
        }

        mSudokuBoard.setGame(mSudokuGame);
        mSudokuGame.setOnPuzzleSolvedListener(onSolvedListener);

        mControlPanel = findViewById(R.id.input_methods);
        mControlPanel.initialize(mSudokuBoard, mSudokuGame);
        mNumpad = mControlPanel.getInputMethod();

        mControlPanelPersister = new ControlPanelPersister(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v("onResume","on");

        // read game settings
        SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //int screenPadding = gameSettings.getInt("screen_border_size", 0);

        String theme = gameSettings.getString("theme", "default");
        if (theme.equals("custom")) {
            mSudokuBoard.setLineColor(gameSettings.getInt("custom_theme_lineColor", R.color.default_lineColor));
            mSudokuBoard.setSectorLineColor(gameSettings.getInt("custom_theme_sectorLineColor", R.color.default_sectorLineColor));
            mSudokuBoard.setTextColor(gameSettings.getInt("custom_theme_textColor", R.color.default_textColor));
            mSudokuBoard.setTextColorReadOnly(gameSettings.getInt("custom_theme_textColorReadOnly", R.color.default_textColorReadOnly));
            mSudokuBoard.setTextColorNote(gameSettings.getInt("custom_theme_textColorNote", R.color.default_textColorNote));
            mSudokuBoard.setBackgroundColor(gameSettings.getInt("custom_theme_backgroundColor", R.color.default_backgroundColor));
            mSudokuBoard.setBackgroundColorSecondary(gameSettings.getInt("custom_theme_backgroundColorSecondary", R.color.default_backgroundColorSecondary));
            mSudokuBoard.setBackgroundColorReadOnly(gameSettings.getInt("custom_theme_backgroundColorReadOnly", R.color.default_backgroundColorReadOnly));
            mSudokuBoard.setBackgroundColorTouched(gameSettings.getInt("custom_theme_backgroundColorTouched", R.color.default_backgroundColorTouched));
            mSudokuBoard.setBackgroundColorSelected(gameSettings.getInt("custom_theme_backgroundColorSelected", R.color.default_backgroundColorSelected));
            mSudokuBoard.setBackgroundColorHighlighted(gameSettings.getInt("custom_theme_backgroundColorHighlighted", R.color.default_backgroundColorHighlighted));
        }

        mSudokuBoard.setHighlightWrongVals(gameSettings.getBoolean("highlight_wrong_values", true));
        mSudokuBoard.setHighlightTouchedCell(gameSettings.getBoolean("highlight_touched_cell", true));
        mSudokuBoard.setHighlightSimilarCell(gameSettings.getBoolean("highlight_similar_cells", true));

        mShowTime = gameSettings.getBoolean("show_time", true);
        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_PLAYING) {
            mSudokuGame.resume();

            if (mShowTime) {
                mGameTimer.start();
            }
        }
        mTimeLabel.setVisibility(mFullScreen && mShowTime ? View.VISIBLE : View.GONE);

        mNumpad.setEnabled(gameSettings.getBoolean("im_numpad", true));
        mNumpad.setMoveCellSelectionOnPress(gameSettings.getBoolean("im_numpad_move_right", false));
        mNumpad.setHighlightCompletedValues(gameSettings.getBoolean("highlight_completed_values", true));
        mNumpad.setShowNumberTotals(gameSettings.getBoolean("show_number_totals", false));

        mSudokuBoard.invokeOnCellSelected();

        updateTime();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v("onWindowFocusChanged","on");
        if (hasFocus) {
            // empty space at the top of the screen). This is desperate workaround.
            if (mFullScreen) {
                mGuiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                        mRootLayout.requestLayout();
                    }
                }, 1000);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause","on");
        // we will save game to the database as we might not be able to get back
        mDatabase.updateSudoku(mSudokuGame);

        mGameTimer.stop();
        mControlPanel.pause();
        mControlPanelPersister.saveState(mControlPanel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("onDestroy","on");
        mDatabase.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("onSaveInstanceState","on");
        mGameTimer.stop();

        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_PLAYING) {
            mSudokuGame.pause();
        }

        mSudokuGame.saveState(outState);
        mGameTimer.saveState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("onCreateOptionsMenu","on");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("onOptionsItemSelected","on");
        switch (item.getItemId()) {
            case R.id.restart:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Do you want to restart the game?");
                builder1.setCancelable(true);

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                mSudokuGame.reset();
                                mSudokuGame.start();
                                mSudokuBoard.setReadOnly(false);
                                if (mShowTime) {
                                    mGameTimer.start();
                                }
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;

            case R.id.easy:
                final int random1 = new Random().nextInt(99) + 1;
                onRecreate(random1);
                onResume();
                return true;

            case R.id.medium:
                final int random2 = new Random().nextInt(99) + 100;
                onRecreate(random2);
                onResume();
                return true;

            case R.id.hard:
                final int random3 = new Random().nextInt(29) + 200;
                onRecreate(random3);
                onResume();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRecreate(int Sudoku_id) {
        AndroidUtils.setThemeFromPreferences(this);

        setContentView(R.layout.activity_main);

        mRootLayout =findViewById(R.id.root_layout);
        mSudokuBoard = findViewById(R.id.sudoku_board);
        mTimeLabel = findViewById(R.id.time_label);

        mDatabase = new Database(getApplicationContext());
        mGameTimer = new GameTimer();
        mGuiHandler = new Handler();

        mSudokuGame = mDatabase.getSudoku(Sudoku_id);
        mSudokuGame.start();

        if (mSudokuGame.getState() == SudokuGame.GAME_STATE_COMPLETED) {
            mSudokuBoard.setReadOnly(true);
        }

        mSudokuBoard.setGame(mSudokuGame);
        mSudokuGame.setOnPuzzleSolvedListener(onSolvedListener);
        mControlPanel = findViewById(R.id.input_methods);
        mControlPanel.initialize(mSudokuBoard, mSudokuGame);
        mNumpad = mControlPanel.getInputMethod();
        mControlPanelPersister = new ControlPanelPersister(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                restartActivity();
                break;
        }
    }

    // Restarts whole activity.
    private void restartActivity() {
        startActivity(getIntent());
        finish();
    }

    private void createDailog(String s) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(s);
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Occurs when puzzle is solved.
    private OnPuzzleSolvedListener onSolvedListener = new OnPuzzleSolvedListener() {

        @Override
        public void onPuzzleSolved() {
            if (mShowTime) {
                mGameTimer.stop();
            }
            mSudokuBoard.setReadOnly(true);
            createDailog("Well Done!");
        }
    };

    // Update the time of game-play.
    void updateTime() {
        if (mShowTime) {
            setTitle(mGameTimeFormatter.format(mSudokuGame.getTime()));
            mTimeLabel.setText(mGameTimeFormatter.format(mSudokuGame.getTime()));
        } else {
            setTitle(R.string.app_name);
        }
    }

    private final class GameTimer extends Time {
        GameTimer() {
            super(1000);
        }

        @Override
        protected boolean step(int count, long time) {
            updateTime();
            return false;
        }
    }
}
