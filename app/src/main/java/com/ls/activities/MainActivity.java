package com.ls.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ftunram.secsurf.core.pornFiltering;
import com.ftunram.secsurf.core.svmPornFiltering;
import com.ftunram.secsurf.toolkit.Asset2file;
import com.ftunram.secsurf.toolkit.FileEditor;
import com.ftunram.secsurf.toolkit.FileRWan;
import com.ftunram.secsurf.toolkit.ImageCounter;
import com.ls.directoryselector.DirectoryDialog;
import com.ls.directoryselector.DirectoryDialog.Listener;
import com.ls.directoryselectordemo.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.BuildConfig;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.ml.EM;
import org.opencv.objdetect.CascadeClassifier;

public class MainActivity extends ListActivity implements Listener {
    private static final String TAG = "OCVSample::Activity";
    ArrayAdapter<String> adapter;
    private final OnClickListener clickListener;
    public CascadeClassifier faceDetector;
    ArrayList<String> files;
    ImageCounter ic;
    ArrayList<String> listItems;
    protected BaseLoaderCallback mLoaderCallback;
    svmPornFiltering myEngine;
    pornFiltering scanner;
    ArrayList<Boolean> scanres;
    private AppSettings settings;
    private final OnSharedPreferenceChangeListener sharedPrefsChangeListener;
    private TextView txtDirLocation;
    Activity what;

    /* renamed from: com.ls.activities.MainActivity.1 */
    class C00951 implements OnSharedPreferenceChangeListener {
        C00951() {
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            MainActivity.this.settings.load();
            MainActivity.this.fillViews();
        }
    }

    /* renamed from: com.ls.activities.MainActivity.2 */
    class C00962 implements OnClickListener {
        C00962() {
        }

        public void onClick(View v) {
            if (v.getId() == R.id.btn_change_dir) {
                DirectoryDialog.newInstance(MainActivity.this.settings.getStorePath()).show(MainActivity.this.getFragmentManager(), "directoryDialog");
            }
        }
    }

    /* renamed from: com.ls.activities.MainActivity.3 */
    class C01633 extends BaseLoaderCallback {
        C01633(Context x0) {
            super(x0);
        }

        public void onManagerConnected(int status) {
            switch (status) {
                case EM.START_AUTO_STEP /*0*/:
                    MainActivity.this.initxml();
                    File xm = new File(Environment.getExternalStorageDirectory().toString(), "/secsurf/mySVM.xml");
                    MainActivity.this.myEngine = new svmPornFiltering();
                    MainActivity.this.myEngine.initSVM(xm.getAbsolutePath());
                    Toast.makeText(MainActivity.this.getApplicationContext(), "SVM  data loaded successfully.... ", 1).show();
                default:
                    super.onManagerConnected(status);
            }
        }
    }

    public MainActivity() {
        this.listItems = new ArrayList();
        this.sharedPrefsChangeListener = new C00951();
        this.clickListener = new C00962();
        this.ic = new ImageCounter();
        this.scanner = new pornFiltering();
        this.what = this;
        this.mLoaderCallback = new C01633(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.settings = AppSettings.getSettings((Activity) this);
        initViews();
        fillViews();
        this.adapter = new ArrayAdapter(this, 17367043, this.listItems);
        setListAdapter(this.adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(item);
        }
        SettingsActivity.startThisActivity(this);
        return true;
    }

    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
    }

    public void onResume() {
        super.onResume();
        this.settings.load();
        fillViews();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, this.mLoaderCallback);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
    }

    public void onDirectorySelected(File dir) {
        this.settings.setStorePath(dir.getPath());
        this.settings.saveDeferred();
        fillViews();
    }

    public void onCancelled() {
    }

    private void initViews() {
        this.txtDirLocation = (TextView) findViewById(R.id.txt_dir_location);
        ((Button) findViewById(R.id.btn_change_dir)).setOnClickListener(this.clickListener);
        this.scanres = new ArrayList();
    }

    public void clrClick(View v) {
        this.adapter.clear();
        new pornFiltering().scan2("blabla", this);
        this.scanres.clear();
    }

    public void delClick(View v) {
        for (int i = 0; i < this.scanres.size(); i++) {
            if (((Boolean) this.scanres.get(i)).booleanValue()) {
                Log.i("test make dir", BuildConfig.FLAVOR + this.settings.getStorePath() + "/" + ((String) this.files.get(i)) + FileEditor.delete(this.settings.getStorePath() + "/" + ((String) this.files.get(i))));
            }
        }
    }

    public void scanClick(View v) {
        FileRWan write = new FileRWan();
        this.files = this.ic.getNumFiles(this.settings.getStorePath());
        ((Button) findViewById(R.id.button2)).setEnabled(true);
        ((Button) findViewById(R.id.button3)).setEnabled(true);
        File x = new File(this.settings.getStorePath() + "/protected.ini");
        try {
            x.createNewFile();
            Log.i("test make dir", "sukses");
        } catch (Exception e) {
            Log.i("test make dir", e.getMessage().toString());
        }
        String out = BuildConfig.FLAVOR;
        for (int i = 0; i < this.files.size(); i++) {
            Log.i("test make dir", this.settings.getStorePath() + "/" + ((String) this.files.get(i)));
            Boolean temp = Boolean.valueOf(this.myEngine.matchSVM(this.settings.getStorePath() + "/" + ((String) this.files.get(i)), null));
            if (temp.booleanValue()) {
                this.listItems.add(((String) this.files.get(i)) + "\nResult : " + "Negative Content!");
                this.scanres.add(Boolean.valueOf(true));
            } else {
                this.listItems.add(((String) this.files.get(i)) + "\nResult : " + "Good Content");
                this.scanres.add(Boolean.valueOf(false));
            }
            out = out + BuildConfig.FLAVOR + temp;
        }
        Log.i("isi ini", out);
        write.write(this, out, x);
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

    private void fillViews() {
        this.txtDirLocation.setText(this.settings.getStorePath());
    }
}
