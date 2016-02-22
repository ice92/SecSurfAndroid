package com.ls.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {
    private static final String INITIALIZED_KEY = "initialized";
    private static final String STORE_PATH_KEY = "store_path";
    private boolean initialized;
    private String storePath;

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getStorePath() {
        return this.storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public void load(SharedPreferences prefs) {
        this.initialized = prefs.getBoolean(INITIALIZED_KEY, false);
        this.storePath = prefs.getString(STORE_PATH_KEY, null);
    }

    public void save(SharedPreferences prefs) {
        Editor editor = prefs.edit();
        save(editor);
        editor.commit();
    }

    public void saveDeferred(SharedPreferences prefs) {
        Editor editor = prefs.edit();
        save(editor);
        editor.apply();
    }

    public void save(Editor editor) {
        editor.putBoolean(INITIALIZED_KEY, this.initialized);
        editor.putString(STORE_PATH_KEY, this.storePath);
    }
}
