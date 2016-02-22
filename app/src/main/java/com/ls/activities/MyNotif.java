package com.ls.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ls.directoryselectordemo.R;

public class MyNotif extends Activity {
    Intent autoscan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notif);
        this.autoscan = new Intent(this, Autoscan.class);
        startService(this.autoscan);
    }

    public void finish(View v) {
        stopService(this.autoscan);
    }
}
