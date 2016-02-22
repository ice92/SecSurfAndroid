package com.ls.activities;

import android.app.Application;
import java.io.File;

public class MyApplication extends Application {
    public final AppSettings settings;

    public MyApplication() {
        this.settings = new AppSettings(this);
    }

    public void onCreate() {
        super.onCreate();
        this.settings.load();
        if (!this.settings.isInitialized()) {
            this.settings.setInitialized(true);
            File file = getExternalFilesDir(null);
            if (file != null) {
                this.settings.setStorePath(file.getPath());
            }
            this.settings.save();
        }
    }
}
