package com.ls.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.ftunram.secsurf.core.svmPornFiltering;
import com.ftunram.secsurf.toolkit.Asset2file;
import com.ls.directoryselectordemo.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.BuildConfig;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.ml.EM;
import org.opencv.objdetect.CascadeClassifier;

public class NewMain extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int NOTIFICATION_ID = 13;
    private static final int RQS_OPEN_DOCUMENT_TREE = 2;
    private static final String TAG = "OCVSample::Activity";
    private static final int UI_ANIMATION_DELAY = 300;
    ArrayAdapter<String> adapter;
    Intent auto;
    Button clear;
    Button delete;
    TextView dir;
    DocumentFile documentFile;
    public CascadeClassifier faceDetector;
    ArrayList<String> listItems;
    private View mContentView;
    private View mControlsView;
    private final OnTouchListener mDelayHideTouchListener;
    private final Handler mHideHandler;
    private final Runnable mHidePart2Runnable;
    private final Runnable mHideRunnable;
    protected BaseLoaderCallback mLoaderCallback;
    private NotificationManager mNM;
    private final Runnable mShowPart2Runnable;
    private boolean mVisible;
    svmPornFiltering myEngine;
    ListView myListView;
    ArrayList<String> myStringArray1;
    ArrayList<Boolean> scanres;
    AppSettings settings;
    private final OnSharedPreferenceChangeListener sharedPrefsChangeListener;
    TextView txtDirLocation;
    Activity what;

    /* renamed from: com.ls.activities.NewMain.1 */
    class C00971 implements Runnable {
        C00971() {
        }

        @SuppressLint({"InlinedApi"})
        public void run() {
            NewMain.this.mContentView.setSystemUiVisibility(4871);
        }
    }

    /* renamed from: com.ls.activities.NewMain.2 */
    class C00982 implements Runnable {
        C00982() {
        }

        public void run() {
            ActionBar actionBar = NewMain.this.getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            NewMain.this.mControlsView.setVisibility(0);
        }
    }

    /* renamed from: com.ls.activities.NewMain.3 */
    class C00993 implements Runnable {
        C00993() {
        }

        public void run() {
            NewMain.this.hide();
        }
    }

    /* renamed from: com.ls.activities.NewMain.4 */
    class C01004 implements OnTouchListener {
        C01004() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            NewMain.this.delayedHide(NewMain.AUTO_HIDE_DELAY_MILLIS);
            return false;
        }
    }

    /* renamed from: com.ls.activities.NewMain.5 */
    class C01015 implements OnClickListener {
        C01015() {
        }

        public void onClick(View view) {
            NewMain.this.toggle();
        }
    }

    /* renamed from: com.ls.activities.NewMain.6 */
    class C01026 implements OnSharedPreferenceChangeListener {
        C01026() {
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            NewMain.this.settings.load();
        }
    }

    /* renamed from: com.ls.activities.NewMain.7 */
    class C01647 extends BaseLoaderCallback {
        C01647(Context x0) {
            super(x0);
        }

        public void onManagerConnected(int status) {
            switch (status) {
                case EM.START_AUTO_STEP /*0*/:
                    NewMain.this.initxml();
                    File xm = new File(Environment.getExternalStorageDirectory().toString(), "/secsurf/mySVM.xml");
                    NewMain.this.myEngine = new svmPornFiltering();
                    NewMain.this.myEngine.initSVM(xm.getAbsolutePath());
                    Log.i("test make dir", BuildConfig.FLAVOR + xm.getAbsolutePath());
                default:
                    super.onManagerConnected(status);
            }
        }
    }

    public NewMain() {
        this.mHideHandler = new Handler();
        this.mHidePart2Runnable = new C00971();
        this.mShowPart2Runnable = new C00982();
        this.mHideRunnable = new C00993();
        this.mDelayHideTouchListener = new C01004();
        this.listItems = new ArrayList();
        this.myStringArray1 = new ArrayList();
        this.sharedPrefsChangeListener = new C01026();
        this.what = this;
        this.mLoaderCallback = new C01647(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        this.mVisible = AUTO_HIDE;
        this.mControlsView = findViewById(R.id.fullscreen_content_controls);
        this.mContentView = findViewById(R.id.fullscreen_content);
        this.myListView = (ListView) findViewById(R.id.listView2);
        this.dir = (TextView) findViewById(R.id.textView4);
        this.mContentView.setOnClickListener(new C01015());
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (this.mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        this.mHideHandler.removeCallbacks(this.mShowPart2Runnable);
        this.mHideHandler.postDelayed(this.mHidePart2Runnable, 300);
    }

    @SuppressLint({"InlinedApi"})
    private void show() {
        this.mHideHandler.removeCallbacks(this.mHidePart2Runnable);
        this.mHideHandler.postDelayed(this.mShowPart2Runnable, 300);
    }

    private void delayedHide(int delayMillis) {
        this.mHideHandler.removeCallbacks(this.mHideRunnable);
        this.mHideHandler.postDelayed(this.mHideRunnable, (long) delayMillis);
    }

    public void selfol(View v) {
        startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), RQS_OPEN_DOCUMENT_TREE);
    }

    @TargetApi(21)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.clear = (Button) findViewById(R.id.clearbt);
        this.delete = (Button) findViewById(R.id.deletebt);
        this.clear.setEnabled(AUTO_HIDE);
        this.delete.setEnabled(AUTO_HIDE);
        this.scanres = new ArrayList();
        if (resultCode == -1 && requestCode == RQS_OPEN_DOCUMENT_TREE) {
            this.documentFile = DocumentFile.fromTreeUri(this, data.getData());
            String path = "/storage/extSdCard/" + this.documentFile.getName();
            this.dir.setText(path);
            for (DocumentFile file : this.documentFile.listFiles()) {
                if (!file.isDirectory()) {
                    Log.i("test make dir", BuildConfig.FLAVOR + path + "/" + file.getName());
                    if (this.myEngine.matchSVM(path + "/" + file.getName(), null)) {
                        this.myStringArray1.add(file.getName() + "\nResult : " + "Negative Content!");
                        this.scanres.add(Boolean.valueOf(AUTO_HIDE));
                    } else {
                        this.myStringArray1.add(file.getName() + "\nResult : " + "Good Content");
                        this.scanres.add(Boolean.valueOf(false));
                    }
                }
            }
            this.adapter = new ArrayAdapter(this, 17367043, this.myStringArray1);
            this.myListView.setAdapter(this.adapter);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void clrClick(View v) {
        this.adapter.clear();
        this.scanres.clear();
    }

    public void delClick(View v) {
        int i = 0;
        boolean deleteall = get50Percent();
        for (DocumentFile file : this.documentFile.listFiles()) {
            if (((Boolean) this.scanres.get(i)).booleanValue() || deleteall) {
                file.delete();
                i++;
            } else {
                i++;
            }
        }
        this.adapter.clear();
        this.scanres.clear();
    }

    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
    }

    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, this.mLoaderCallback);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
    }

    private void initxml() {
        if (!new File(Environment.getExternalStorageDirectory() + File.separator + "secsurf", "mySVM.xml").exists() && Environment.getExternalStorageState().equals("mounted")) {
            try {
                new Asset2file().createFileFromInputStream(this.what.getAssets().open("mySVM.xml"));
            } catch (IOException e) {
                Log.i("test make dir", "gagal copi file");
            }
        }
    }

    public void showNotif(View v) {
        this.auto = new Intent(this, Autoscan.class);
        startService(this.auto);
    }

    public void hideNOTIF(View v) {
        stopService(this.auto);
    }

    public boolean get50Percent() {
        double x = (double) this.scanres.size();
        double y = 0.0d;
        for (int i = 0; ((double) i) < x; i++) {
            if (((Boolean) this.scanres.get(i)).booleanValue()) {
                y += 1.0d;
            }
        }
        if (y / x >= 0.5d) {
            return AUTO_HIDE;
        }
        return false;
    }
}
