package com.ls.activities;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import com.ftunram.secsurf.core.svmPornFiltering;
import com.ftunram.secsurf.toolkit.Asset2file;
import com.ftunram.secsurf.toolkit.ImageCounter;
import com.ftunram.secsurf.toolkit.Utils;
import com.ls.directoryselectordemo.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.ml.EM;

public class Autoscan extends IntentService {
    ArrayList<String> files;
    ArrayList<String> files2;
    ArrayList<String> files3;
    ImageCounter ic;
    protected BaseLoaderCallback mLoaderCallback;
    svmPornFiltering myEngine;
    private final String[] okFileExtensions;
    ArrayList<String> scanresdcim;
    ArrayList<String> scanresdown;
    ArrayList<String> scanrespict;

    /* renamed from: com.ls.activities.Autoscan.1 */
    class C01621 extends BaseLoaderCallback {
        C01621(Context x0) {
            super(x0);
        }

        public void onManagerConnected(int status) {
            switch (status) {
                case EM.START_AUTO_STEP /*0*/:
                    Autoscan.this.initxml();
                    File xm = new File(Environment.getExternalStorageDirectory().toString(), "/secsurf/mySVM.xml");
                    Autoscan.this.myEngine = new svmPornFiltering();
                    Autoscan.this.myEngine.initSVM(xm.getAbsolutePath());
                default:
                    super.onManagerConnected(status);
            }
        }
    }

    public Autoscan() {
        super("AutoScan");
        this.ic = new ImageCounter();
        this.mLoaderCallback = new C01621(this);
        this.okFileExtensions = new String[]{Utils.jpg, Utils.png, Utils.gif, Utils.jpeg};
    }

    protected void onHandleIntent(Intent intent) {
        Notification n = new Notification();
        n.icon = R.mipmap.ic_launcher;
        n.tickerText = "Secsurf Auto Scan Running";
        n.when = System.currentTimeMillis();
        CharSequence contentTitle = "SecSurf";
        n.setLatestEventInfo(this, contentTitle, "Auto scan running", PendingIntent.getActivity(this, 0, new Intent(this, MyNotif.class), 0));
        startForeground(1, n);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, this.mLoaderCallback);
        File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        File cameraDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        File blueDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        while (true) {
            int i;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.scanresdown = new ArrayList();
            this.scanresdcim = new ArrayList();
            this.scanrespict = new ArrayList();
            this.files = this.ic.getNumFiles(downloadDir.getPath());
            this.files2 = this.ic.getNumFiles(cameraDir.getPath());
            this.files3 = this.ic.getNumFiles(blueDir.getPath());
            for (i = 0; i < this.files.size(); i++) {
                Boolean temp1 = Boolean.valueOf(this.myEngine.matchSVM(downloadDir.getPath() + "/" + ((String) this.files.get(i)), null));
                Log.i("test make dir", (String) this.files.get(i));
                if (temp1.booleanValue()) {
                    this.scanresdown.add(this.files.get(i));
                }
            }
            for (i = 0; i < this.files2.size(); i++) {
                if (Boolean.valueOf(this.myEngine.matchSVM(cameraDir.getPath() + "/" + ((String) this.files2.get(i)), null)).booleanValue()) {
                    this.scanresdcim.add(this.files2.get(i));
                }
            }
            for (i = 0; i < this.files3.size(); i++) {
                if (Boolean.valueOf(this.myEngine.matchSVM(blueDir.getPath() + "/" + ((String) this.files3.get(i)), null)).booleanValue()) {
                    this.scanrespict.add(this.files3.get(i));
                }
            }
            getFilesFromDir(downloadDir);
            getFilesFromDir1(cameraDir);
            getFilesFromDir2(blueDir);
        }
    }

    public void getFilesFromDir(File filesFromSD) {
        DocumentFile file = DocumentFile.fromFile(filesFromSD);
        int i = 0;
        if (this.scanresdown.size() > 0) {
            for (DocumentFile f : file.listFiles()) {
                if (!f.isDirectory()) {
                    Log.i("test make dir", f.getName());
                    if (((String) this.scanresdown.get(i)).equals(f.getName())) {
                        f.delete();
                        i++;
                    }
                }
            }
        }
    }

    public void getFilesFromDir1(File filesFromSD) {
        DocumentFile file = DocumentFile.fromFile(filesFromSD);
        int i = 0;
        if (this.scanresdcim.size() > 0) {
            for (DocumentFile f : file.listFiles()) {
                if (!f.isDirectory() && ((String) this.scanresdcim.get(i)).equals(f.getName())) {
                    f.delete();
                    i++;
                }
            }
        }
    }

    public void getFilesFromDir2(File filesFromSD) {
        DocumentFile file = DocumentFile.fromFile(filesFromSD);
        int i = 0;
        if (this.scanrespict.size() > 0) {
            for (DocumentFile f : file.listFiles()) {
                if (!f.isDirectory() && ((String) this.scanrespict.get(i)).equals(f.getName())) {
                    f.delete();
                    i++;
                }
            }
        }
    }

    private void initxml() {
        if (!new File(Environment.getExternalStorageDirectory() + File.separator + "secsurf", "mySVM.xml").exists() && Environment.getExternalStorageState().equals("mounted")) {
            try {
                new Asset2file().createFileFromInputStream(getAssets().open("mySVM.xml"));
            } catch (IOException e) {
                Log.i("test make dir", "gagal copi file");
            }
        }
    }

    public boolean isImage(DocumentFile file) {
        for (String extension : this.okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
