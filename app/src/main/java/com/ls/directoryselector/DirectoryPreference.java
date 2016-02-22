package com.ls.directoryselector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.DialogPreference;
import android.preference.Preference.BaseSavedState;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.ls.directoryselector.DirectorySelector.Callback;
import java.io.File;

public class DirectoryPreference extends DialogPreference {
    private AlertDialog dialog;
    private final DirectorySelector dirChooser;
    private final Callback dirSelectorCallback;

    /* renamed from: com.ls.directoryselector.DirectoryPreference.3 */
    class C01083 implements TextWatcher {
        C01083() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (DirectoryPreference.this.dialog != null) {
                DirectoryPreference.this.dialog.getButton(-1).setEnabled(!s.toString().trim().isEmpty());
            }
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryPreference.4 */
    class C01094 implements OnClickListener {
        final /* synthetic */ EditText val$input;

        C01094(EditText editText) {
            this.val$input = editText;
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            String dirName = this.val$input.getText().toString().trim();
            if (!dirName.isEmpty()) {
                DirectoryPreference.this.dirChooser.createFolder(dirName);
            }
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryPreference.5 */
    class C01105 implements OnClickListener {
        C01105() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        public final Bundle dialogState;
        public final String selectedDir;

        /* renamed from: com.ls.directoryselector.DirectoryPreference.SavedState.1 */
        static class C01111 implements Creator<SavedState> {
            C01111() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcelable superState, String selectedDir, Bundle dialogState) {
            super(superState);
            this.selectedDir = selectedDir;
            this.dialogState = dialogState;
        }

        public SavedState(Parcel source) {
            super(source);
            this.selectedDir = source.readString();
            this.dialogState = source.readBundle();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.selectedDir);
            dest.writeBundle(this.dialogState);
        }

        static {
            CREATOR = new C01111();
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryPreference.1 */
    class C01561 implements Callback {
        C01561() {
        }

        public void onNewDirButtonClicked() {
            DirectoryPreference.this.createNewFolderDialog(null);
        }
    }

    /* renamed from: com.ls.directoryselector.DirectoryPreference.2 */
    class C01572 extends DirectorySelector {
        C01572(Callback callback) {
            super(callback);
        }

        protected Context getContext() {
            return DirectoryPreference.this.getContext();
        }

        protected File getInitialDirectory() {
            File ret = null;
            String value = DirectoryPreference.this.getPersistedString(null);
            if (value != null) {
                File file = new File(value);
                if (file.exists() && file.isDirectory()) {
                    ret = file;
                }
            }
            if (ret == null) {
                return Environment.getExternalStorageDirectory();
            }
            return ret;
        }
    }

    public DirectoryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.dirSelectorCallback = new C01561();
        this.dirChooser = new C01572(this.dirSelectorCallback);
        init(context);
    }

    public DirectoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.dirSelectorCallback = new C01561();
        this.dirChooser = new C01572(this.dirSelectorCallback);
        init(context);
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.dirChooser.initViews(view);
    }

    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            String value = this.dirChooser.getSelectedDir().getPath();
            persistString(value);
            callChangeListener(value);
        }
    }

    private void init(Context context) {
        setPersistent(true);
        setDialogTitle(null);
        setDialogLayoutResource(this.dirChooser.getViewResId());
        setPositiveButtonText(17039370);
        setNegativeButtonText(17039360);
    }

    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.dirChooser.onResume();
    }

    public void onDismiss(DialogInterface dialog) {
        this.dirChooser.onPause();
        super.onDismiss(dialog);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        File selectedDir = this.dirChooser.getSelectedDir();
        if (selectedDir == null) {
            return superState;
        }
        return new SavedState(superState, selectedDir.getPath(), this.dialog == null ? null : this.dialog.onSaveInstanceState());
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        this.dirChooser.setSelectedDir(myState.selectedDir);
        if (myState.dialogState != null) {
            createNewFolderDialog(myState.dialogState);
        }
    }

    public void onActivityDestroy() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        super.onActivityDestroy();
    }

    private void createNewFolderDialog(Bundle savedState) {
        View view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0120R.layout.edit_text_layout, null);
        EditText input = (EditText) view.findViewById(C0120R.id.edit_value);
        input.addTextChangedListener(new C01083());
        this.dialog = new Builder(getContext()).setTitle(C0120R.string.create_folder).setMessage(C0120R.string.create_folder_msg).setView(view).setNegativeButton(17039360, new C01105()).setPositiveButton(17039370, new C01094(input)).create();
        if (savedState != null) {
            this.dialog.onRestoreInstanceState(savedState);
        }
        this.dialog.show();
        this.dialog.getButton(-1).setEnabled(!input.getText().toString().trim().isEmpty());
    }
}
