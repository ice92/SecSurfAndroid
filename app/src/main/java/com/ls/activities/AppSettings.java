package com.ls.activities;

import android.app.Activity;
import android.app.Application;
import android.preference.PreferenceManager;

public class AppSettings extends Settings {
    private final MyApplication application;

    public static AppSettings getSettings(Activity activity) {
        return getSettings(activity.getApplication());
    }

    public static AppSettings getSettings(Application application) {
        return ((MyApplication) application).settings;
    }

    public AppSettings(MyApplication application) {
        this.application = application;
    }

    public void load() {
        load(PreferenceManager.getDefaultSharedPreferences(this.application));
    }

    public void save() {
        save(PreferenceManager.getDefaultSharedPreferences(this.application));
    }

    public void saveDeferred() {
        saveDeferred(PreferenceManager.getDefaultSharedPreferences(this.application));
    }
}
