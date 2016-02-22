package com.ls.directoryselector;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import com.ls.directoryselector.DirectorySelector.Callback;
import java.io.File;

public class DirectoryDialog extends DialogFragment {
    private static final int CREATE_DIR_CODE = 1000;
    private static final String INITIAL_DIR_KEY = "initial_dir";
    private static final String SELECTED_DIR_KEY = "selected_dir";
    private final DirectorySelector dirChooser;
    private final Callback dirSelectorCallback;
    private Listener listener;
    private String selectedDir;

    /* renamed from: com.ls.directoryselector.DirectoryDialog.3 */
    class C01063 implements OnClickListener {
        C01063() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            if (DirectoryDialog.this.listener != null) {
                DirectoryDialog.this.listener.onCancelled();
            }
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryDialog.4 */
    class C01074 implements OnClickListener {
        C01074() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            if (DirectoryDialog.this.listener != null) {
                DirectoryDialog.this.listener.onDirectorySelected(DirectoryDialog.this.dirChooser.getSelectedDir());
            }
        }
    }

    public interface Listener {
        void onCancelled();

        void onDirectorySelected(File file);
    }

    /* renamed from: com.ls.directoryselector.DirectoryDialog.1 */
    class C01541 implements Callback {
        C01541() {
        }

        public void onNewDirButtonClicked() {
            EditTextDialog.newInstance(DirectoryDialog.this, DirectoryDialog.CREATE_DIR_CODE, DirectoryDialog.this.getString(C0120R.string.create_folder), DirectoryDialog.this.getString(C0120R.string.create_folder_msg)).show(DirectoryDialog.this.getFragmentManager(), "createDirDialog");
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryDialog.2 */
    class C01552 extends DirectorySelector {
        C01552(Callback callback) {
            super(callback);
        }

        protected Context getContext() {
            return DirectoryDialog.this.getActivity();
        }

        protected File getInitialDirectory() {
            return Environment.getExternalStorageDirectory();
        }
    }

    public DirectoryDialog() {
        this.dirSelectorCallback = new C01541();
        this.dirChooser = new C01552(this.dirSelectorCallback);
    }

    public static DirectoryDialog newInstance(String initialDirectory) {
        DirectoryDialog ret = new DirectoryDialog();
        Bundle args = new Bundle();
        args.putString(INITIAL_DIR_KEY, initialDirectory);
        ret.setArguments(args);
        return ret;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        File selectedDir = this.dirChooser.getSelectedDir();
        if (selectedDir != null) {
            outState.putString(SELECTED_DIR_KEY, selectedDir.getPath());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedDir = null;
        if (getArguments() != null) {
            this.selectedDir = getArguments().getString(INITIAL_DIR_KEY);
        }
        if (savedInstanceState != null) {
            this.selectedDir = savedInstanceState.getString(SELECTED_DIR_KEY);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity()).setPositiveButton(17039370, new C01074()).setNegativeButton(17039360, new C01063());
        View view = getActivity().getLayoutInflater().inflate(this.dirChooser.getViewResId(), null);
        this.dirChooser.initViews(view);
        if (!TextUtils.isEmpty(this.selectedDir)) {
            this.dirChooser.setSelectedDir(this.selectedDir);
        }
        builder.setView(view);
        return builder.create();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            this.listener = (Listener) activity;
        }
    }

    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    public void onPause() {
        super.onPause();
        this.dirChooser.onPause();
    }

    public void onResume() {
        super.onResume();
        this.dirChooser.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREATE_DIR_CODE /*1000*/:
                if (resultCode == -1 && data != null) {
                    this.dirChooser.createFolder(data.getStringExtra(EditTextDialog.EDIT_VALUE_KEY));
                }
            default:
        }
    }
}
