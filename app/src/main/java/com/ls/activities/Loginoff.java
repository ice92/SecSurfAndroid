package com.ls.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;
import com.ftunram.secsurf.toolkit.DatabaseHelper;
import com.ls.directoryselectordemo.R;

public class Loginoff extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    DatabaseHelper helper;
    private View mContentView;
    private View mControlsView;
    private final OnTouchListener mDelayHideTouchListener;
    private final Handler mHideHandler;
    private final Runnable mHidePart2Runnable;
    private final Runnable mHideRunnable;
    private final Runnable mShowPart2Runnable;
    private boolean mVisible;

    /* renamed from: com.ls.activities.Loginoff.1 */
    class C00901 implements Runnable {
        C00901() {
        }

        @SuppressLint({"InlinedApi"})
        public void run() {
            Loginoff.this.mContentView.setSystemUiVisibility(4871);
        }
    }

    /* renamed from: com.ls.activities.Loginoff.2 */
    class C00912 implements Runnable {
        C00912() {
        }

        public void run() {
            ActionBar actionBar = Loginoff.this.getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            Loginoff.this.mControlsView.setVisibility(0);
        }
    }

    /* renamed from: com.ls.activities.Loginoff.3 */
    class C00923 implements Runnable {
        C00923() {
        }

        public void run() {
            Loginoff.this.hide();
        }
    }

    /* renamed from: com.ls.activities.Loginoff.4 */
    class C00934 implements OnTouchListener {
        C00934() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            Loginoff.this.delayedHide(Loginoff.AUTO_HIDE_DELAY_MILLIS);
            return false;
        }
    }

    /* renamed from: com.ls.activities.Loginoff.5 */
    class C00945 implements OnClickListener {
        C00945() {
        }

        public void onClick(View view) {
            Loginoff.this.toggle();
        }
    }

    public Loginoff() {
        this.mHideHandler = new Handler();
        this.mHidePart2Runnable = new C00901();
        this.mShowPart2Runnable = new C00912();
        this.mHideRunnable = new C00923();
        this.mDelayHideTouchListener = new C00934();
        this.helper = new DatabaseHelper(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginoff);
        this.mVisible = AUTO_HIDE;
        this.mControlsView = findViewById(R.id.fullscreen_content_controls);
        this.mContentView = findViewById(R.id.fullscreen_content);
        this.mContentView.setOnClickListener(new C00945());
        findViewById(R.id.button7).setOnTouchListener(this.mDelayHideTouchListener);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (this.mVisible) {
            hide();
        } else {
            hide();
        }
    }

    private void hide() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        this.mControlsView.setVisibility(8);
        this.mVisible = false;
        this.mHideHandler.removeCallbacks(this.mShowPart2Runnable);
        this.mHideHandler.postDelayed(this.mHidePart2Runnable, 300);
    }

    @SuppressLint({"InlinedApi"})
    private void show() {
        this.mContentView.setSystemUiVisibility(1536);
        this.mVisible = AUTO_HIDE;
        this.mHideHandler.removeCallbacks(this.mHidePart2Runnable);
        this.mHideHandler.postDelayed(this.mShowPart2Runnable, 300);
    }

    private void delayedHide(int delayMillis) {
        this.mHideHandler.removeCallbacks(this.mHideRunnable);
        this.mHideHandler.postDelayed(this.mHideRunnable, (long) delayMillis);
    }

    public void loginClick(View v) {
        String passdb;
        EditText password = (EditText) findViewById(R.id.passin);
        String user = ((EditText) findViewById(R.id.userin)).getText().toString();
        String pass = password.getText().toString();
        String defaut = "master";
        if (checkDataBase(DatabaseHelper.DATABASE_NAME)) {
            passdb = this.helper.searchPass(user);
        } else {
            passdb = defaut;
        }
        if (pass.equals(passdb)) {
            Intent intent;
            if (VERSION.SDK_INT >= 19) {
                intent = new Intent(this, NewMain.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            return;
        }
        Toast.makeText(this, "Password/Username salah!", 0).show();
    }

    private boolean checkDataBase(String dbname) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbname, null, 1);
            checkDB.close();
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            return AUTO_HIDE;
        }
        return false;
    }
}
