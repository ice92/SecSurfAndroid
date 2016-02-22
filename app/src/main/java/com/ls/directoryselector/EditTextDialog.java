package com.ls.directoryselector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EditTextDialog extends DialogFragment {
    public static final String EDIT_VALUE_KEY = "value";
    private static final String MESSAGE_KEY = "message";
    private static final String TITLE_KEY = "title";
    private AlertDialog dialog;
    private String message;
    private String title;

    /* renamed from: com.ls.directoryselector.EditTextDialog.1 */
    class C01171 implements TextWatcher {
        C01171() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (EditTextDialog.this.dialog != null) {
                EditTextDialog.this.dialog.getButton(-1).setEnabled(!s.toString().trim().isEmpty());
            }
        }
    }

    /* renamed from: com.ls.directoryselector.EditTextDialog.2 */
    class C01182 implements OnClickListener {
        final /* synthetic */ EditText val$input;

        C01182(EditText editText) {
            this.val$input = editText;
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            EditTextDialog.this.getTargetFragment().onActivityResult(EditTextDialog.this.getTargetRequestCode(), -1, EditTextDialog.getReturnIntent(this.val$input.getText().toString().trim()));
        }
    }

    /* renamed from: com.ls.directoryselector.EditTextDialog.3 */
    class C01193 implements OnClickListener {
        C01193() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            EditTextDialog.this.getTargetFragment().onActivityResult(EditTextDialog.this.getTargetRequestCode(), 0, null);
        }
    }

    public static EditTextDialog newInstance(Fragment targetFragment, int requestCode, String title, String message) {
        EditTextDialog ret = new EditTextDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        ret.setArguments(args);
        ret.setTargetFragment(targetFragment, requestCode);
        return ret;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.title = getArguments().getString(TITLE_KEY);
            this.message = getArguments().getString(MESSAGE_KEY);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(C0120R.layout.edit_text_layout, null);
        EditText input = (EditText) view.findViewById(C0120R.id.edit_value);
        input.addTextChangedListener(new C01171());
        this.dialog = new Builder(getActivity()).setTitle(this.title).setMessage(this.message).setView(view).setNegativeButton(17039360, new C01193()).setPositiveButton(17039370, new C01182(input)).create();
        this.dialog.show();
        this.dialog.getButton(-1).setEnabled(false);
        return this.dialog;
    }

    private static Intent getReturnIntent(String result) {
        Intent ret = new Intent();
        ret.putExtra(EDIT_VALUE_KEY, result);
        return ret;
    }
}
