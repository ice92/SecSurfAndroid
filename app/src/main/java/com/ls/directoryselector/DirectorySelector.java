package com.ls.directoryselector;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.FileObserver;
import android.os.Handler;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ls.directoryselector.utils.DirectoryFileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

abstract class DirectorySelector {
    private static final String TAG;
    private ImageButton btnCreateFolder;
    private ImageButton btnNavUp;
    private final Callback callback;
    private final OnClickListener clickListener;
    private FileObserver fileObserver;
    private final ArrayList<File> files;
    private final Handler handler;
    private FileAdapter listAdapter;
    private final OnItemClickListener listClickListener;
    private ListView listDirectories;
    private File selectedDir;
    private TextView txtSelectedFolder;

    /* renamed from: com.ls.directoryselector.DirectorySelector.1 */
    class C01121 implements OnItemClickListener {
        C01121() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            DirectorySelector.this.changeDirectory((File) DirectorySelector.this.listAdapter.getItem(position));
        }
    }

    /* renamed from: com.ls.directoryselector.DirectorySelector.2 */
    class C01132 implements OnClickListener {
        C01132() {
        }

        public void onClick(View v) {
            if (v.getId() == C0120R.id.btn_nav_up) {
                DirectorySelector.this.changeUp();
            } else if (v.getId() == C0120R.id.btn_create_folder) {
                DirectorySelector.this.callback.onNewDirButtonClicked();
            }
        }
    }

    /* renamed from: com.ls.directoryselector.DirectorySelector.3 */
    class C01163 extends FileObserver {

        /* renamed from: com.ls.directoryselector.DirectorySelector.3.1 */
        class C01141 implements Runnable {
            C01141() {
            }

            public void run() {
                DirectorySelector.this.refreshDirectory();
            }
        }

        /* renamed from: com.ls.directoryselector.DirectorySelector.3.2 */
        class C01152 implements Runnable {
            C01152() {
            }

            public void run() {
                DirectorySelector.this.changeUp();
            }
        }

        C01163(String x0, int x1) {
            super(x0, x1);
        }

        public void onEvent(int event, String path) {
            Log.d(DirectorySelector.TAG, "FileObserver received event " + event);
            if ((event & Video.OPTFLOW_FARNEBACK_GAUSSIAN) != 0 || (event & Calib3d.CALIB_SAME_FOCAL_LENGTH) != 0 || (event & 64) != 0 || (event & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0) {
                DirectorySelector.this.handler.post(new C01141());
            } else if ((event & Imgproc.INTER_TAB_SIZE2) != 0 || (event & Calib3d.CALIB_FIX_K4) != 0) {
                DirectorySelector.this.handler.post(new C01152());
            }
        }
    }

    public interface Callback {
        void onNewDirButtonClicked();
    }

    protected abstract Context getContext();

    protected abstract File getInitialDirectory();

    static {
        TAG = DirectorySelector.class.getSimpleName();
    }

    protected DirectorySelector(Callback callback) {
        this.handler = new Handler();
        this.files = new ArrayList();
        this.listClickListener = new C01121();
        this.clickListener = new C01132();
        this.callback = callback;
    }

    protected int getViewResId() {
        return C0120R.layout.directory_chooser;
    }

    protected void onPause() {
        if (this.fileObserver != null) {
            this.fileObserver.stopWatching();
        }
    }

    protected void onResume() {
        if (this.fileObserver != null) {
            this.fileObserver.startWatching();
        }
    }

    protected File getSelectedDir() {
        return this.selectedDir;
    }

    protected void setSelectedDir(String path) {
        changeDirectory(new File(path));
    }

    protected boolean createFolder(String dirName) {
        if (this.selectedDir == null) {
            showToast(C0120R.string.no_dir_selected);
            return false;
        } else if (this.selectedDir.canWrite()) {
            File newDir = new File(this.selectedDir, dirName);
            if (newDir.exists()) {
                showToast(C0120R.string.error_already_exists);
                return false;
            } else if (newDir.mkdir()) {
                changeDirectory(new File(this.selectedDir, dirName));
                return true;
            } else {
                showToast(C0120R.string.create_folder_error);
                return false;
            }
        } else {
            showToast(C0120R.string.no_write_access);
            return false;
        }
    }

    protected void initViews(View view) {
        this.btnNavUp = (ImageButton) view.findViewById(C0120R.id.btn_nav_up);
        this.btnCreateFolder = (ImageButton) view.findViewById(C0120R.id.btn_create_folder);
        this.txtSelectedFolder = (TextView) view.findViewById(C0120R.id.txt_selected_folder);
        this.listDirectories = (ListView) view.findViewById(C0120R.id.list_dirs);
        this.listDirectories.setEmptyView(view.findViewById(C0120R.id.txt_list_empty));
        this.listDirectories.setOnItemClickListener(this.listClickListener);
        this.btnNavUp.setOnClickListener(this.clickListener);
        this.btnCreateFolder.setOnClickListener(this.clickListener);
        adjustImages();
        this.listAdapter = new FileAdapter(getContext(), this.files);
        this.listDirectories.setAdapter(this.listAdapter);
        changeDirectory(getInitialDirectory());
    }

    private void adjustImages() {
        int color = ViewCompat.MEASURED_SIZE_MASK;
        Theme theme = getContext().getTheme();
        if (theme != null) {
            TypedArray ba = theme.obtainStyledAttributes(new int[]{16842801});
            if (ba != null) {
                color = ba.getColor(0, ViewCompat.MEASURED_SIZE_MASK);
                ba.recycle();
            }
        }
        if (color != ViewCompat.MEASURED_SIZE_MASK && ((0.21d * ((double) Color.red(color))) + (0.71d * ((double) Color.green(color)))) + (0.07d * ((double) Color.blue(color))) < 128.0d) {
            this.btnNavUp.setImageResource(C0120R.drawable.navigation_up_light);
            this.btnCreateFolder.setImageResource(C0120R.drawable.ic_action_create_light);
        }
    }

    private void changeUp() {
        if (this.selectedDir != null) {
            File parent = this.selectedDir.getParentFile();
            if (parent != null) {
                changeDirectory(parent);
            }
        }
    }

    private void changeDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles(new DirectoryFileFilter());
            List<File> filesList = files != null ? Arrays.asList(files) : new ArrayList();
            this.files.clear();
            this.files.addAll(filesList);
            Collections.sort(this.files);
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            this.selectedDir = dir;
            this.txtSelectedFolder.setText(dir.getAbsolutePath());
            if (this.fileObserver != null) {
                this.fileObserver.stopWatching();
            }
            this.fileObserver = createFileObserver(dir.getAbsolutePath());
            this.fileObserver.startWatching();
            Log.d(TAG, "Changed directory to " + dir.getAbsolutePath());
        }
    }

    private void refreshDirectory() {
        if (this.selectedDir != null) {
            changeDirectory(this.selectedDir);
        }
    }

    private FileObserver createFileObserver(String path) {
        return new C01163(path, 4032);
    }

    private void showToast(int resId) {
        Toast.makeText(getContext(), resId, 1).show();
    }
}
