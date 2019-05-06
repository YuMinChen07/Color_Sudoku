package com.yu_minchen.colorsudoku.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;

// This class is responsible for persisting of control panel's state.
public class ControlPanelPersister {

    private static final String PREFIX = ControlPanel.class.getName();

    private SharedPreferences mPreferences;

    public ControlPanelPersister(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveState(ControlPanel controlPanel) {
        // save state of control panel itself
        StateBundle cpState = new StateBundle(mPreferences, PREFIX + "", true);
        cpState.putInt("activeMethodIndex", controlPanel.getActiveMethodIndex());
        cpState.commit();

    }

    public void restoreState(ControlPanel controlPanel) {
        // restore state of control panel itself
        StateBundle cpState = new StateBundle(mPreferences, PREFIX + "", false);
    }

    // This is basically wrapper around anything which is capable of storing
    public static class StateBundle {

        private final SharedPreferences mPreferences;
        private final Editor mPrefEditor;
        private final String mPrefix;
        private final boolean mEditable;

        public StateBundle(SharedPreferences preferences, String prefix,
                           boolean editable) {
            mPreferences = preferences;
            mPrefix = prefix;
            mEditable = editable;

            if (mEditable) {
                mPrefEditor = preferences.edit();
            } else {
                mPrefEditor = null;
            }
        }

        public boolean getBoolean(String key, boolean defValue) {
            return mPreferences.getBoolean(mPrefix + key, defValue);
        }

        public float getFloat(String key, float defValue) {
            return mPreferences.getFloat(mPrefix + key, defValue);
        }

        public int getInt(String key, int defValue) {
            return mPreferences.getInt(mPrefix + key, defValue);
        }

        public String getString(String key, String defValue) {
            return mPreferences.getString(mPrefix + key, defValue);
        }

        public void putBoolean(String key, boolean value) {
            if (!mEditable) {
                throw new IllegalStateException("StateBundle is not editable");
            }
            mPrefEditor.putBoolean(mPrefix + key, value);
        }

        public void putFloat(String key, float value) {
            if (!mEditable) {
                throw new IllegalStateException("StateBundle is not editable");
            }
            mPrefEditor.putFloat(mPrefix + key, value);
        }

        public void putInt(String key, int value) {
            if (!mEditable) {
                throw new IllegalStateException("StateBundle is not editable");
            }
            mPrefEditor.putInt(mPrefix + key, value);
        }

        public void putString(String key, String value) {
            if (!mEditable) {
                throw new IllegalStateException("StateBundle is not editable");
            }
            mPrefEditor.putString(mPrefix + key, value);
        }

        public void commit() {
            if (!mEditable) {
                throw new IllegalStateException("StateBundle is not editable");
            }
            mPrefEditor.commit();
        }
    }
}
