package com.ls.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.Profile;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.ls.directoryselectordemo.R;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    private static final String[] DUMMY_CREDENTIALS;
    private UserLoginTask mAuthTask;
    private AutoCompleteTextView mEmailView;
    private View mLoginFormView;
    private EditText mPasswordView;
    private View mProgressView;

    /* renamed from: com.ls.activities.LoginActivity.1 */
    class C00861 implements OnEditorActionListener {
        C00861() {
        }

        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id != R.id.login && id != 0) {
                return false;
            }
            LoginActivity.this.attemptLogin();
            return true;
        }
    }

    /* renamed from: com.ls.activities.LoginActivity.2 */
    class C00872 implements OnClickListener {
        C00872() {
        }

        public void onClick(View view) {
            LoginActivity.this.attemptLogin();
        }
    }

    /* renamed from: com.ls.activities.LoginActivity.3 */
    class C00883 extends AnimatorListenerAdapter {
        final /* synthetic */ boolean val$show;

        C00883(boolean z) {
            this.val$show = z;
        }

        public void onAnimationEnd(Animator animation) {
            LoginActivity.this.mLoginFormView.setVisibility(this.val$show ? 8 : 0);
        }
    }

    /* renamed from: com.ls.activities.LoginActivity.4 */
    class C00894 extends AnimatorListenerAdapter {
        final /* synthetic */ boolean val$show;

        C00894(boolean z) {
            this.val$show = z;
        }

        public void onAnimationEnd(Animator animation) {
            LoginActivity.this.mProgressView.setVisibility(this.val$show ? 0 : 8);
        }
    }

    private interface ProfileQuery {
        public static final int ADDRESS = 0;
        public static final int IS_PRIMARY = 1;
        public static final String[] PROJECTION;

        static {
            PROJECTION = new String[]{"data1", "is_primary"};
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            this.mEmail = email;
            this.mPassword = password;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                for (String credential : LoginActivity.DUMMY_CREDENTIALS) {
                    String[] pieces = credential.split(":");
                    if (pieces[0].equals(this.mEmail)) {
                        return Boolean.valueOf(pieces[1].equals(this.mPassword));
                    }
                }
                return Boolean.valueOf(true);
            } catch (InterruptedException e) {
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean success) {
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);
            if (success.booleanValue()) {
                LoginActivity.this.finish();
                return;
            }
            LoginActivity.this.mPasswordView.setError(LoginActivity.this.getString(R.string.error_incorrect_password));
            LoginActivity.this.mPasswordView.requestFocus();
        }

        protected void onCancelled() {
            LoginActivity.this.mAuthTask = null;
            LoginActivity.this.showProgress(false);
        }
    }

    public LoginActivity() {
        this.mAuthTask = null;
    }

    static {
        DUMMY_CREDENTIALS = new String[]{"foo@example.com:hello", "bar@example.com:world"};
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        this.mPasswordView = (EditText) findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new C00861());
        ((Button) findViewById(R.id.email_sign_in_button)).setOnClickListener(new C00872());
        this.mLoginFormView = findViewById(R.id.login_form);
        this.mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void attemptLogin() {
        if (this.mAuthTask == null) {
            this.mEmailView.setError(null);
            this.mPasswordView.setError(null);
            String email = this.mEmailView.getText().toString();
            String password = this.mPasswordView.getText().toString();
            boolean cancel = false;
            View focusView = null;
            if (!(TextUtils.isEmpty(password) || isPasswordValid(password))) {
                this.mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = this.mPasswordView;
                cancel = true;
            }
            if (TextUtils.isEmpty(email)) {
                this.mEmailView.setError(getString(R.string.error_field_required));
                focusView = this.mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                this.mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = this.mEmailView;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
                return;
            }
            showProgress(true);
            this.mAuthTask = new UserLoginTask(email, password);
            this.mAuthTask.execute(new Void[]{(Void) null});
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(13)
    private void showProgress(boolean show) {
        float f = 1.0f;
        int i = 8;
        int i2 = 0;
        int i3;
        if (VERSION.SDK_INT >= 13) {
            float f2;
            int shortAnimTime = getResources().getInteger(17694720);
            View view = this.mLoginFormView;
            if (show) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            view.setVisibility(i3);
            ViewPropertyAnimator duration = this.mLoginFormView.animate().setDuration((long) shortAnimTime);
            if (show) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            duration.alpha(f2).setListener(new C00883(show));
            View view2 = this.mProgressView;
            if (!show) {
                i2 = 8;
            }
            view2.setVisibility(i2);
            ViewPropertyAnimator duration2 = this.mProgressView.animate().setDuration((long) shortAnimTime);
            if (!show) {
                f = 0.0f;
            }
            duration2.alpha(f).setListener(new C00894(show));
            return;
        }
        View view3 = this.mProgressView;
        if (show) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        view3.setVisibility(i3);
        view2 = this.mLoginFormView;
        if (!show) {
            i = 0;
        }
        view2.setVisibility(i);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.withAppendedPath(Profile.CONTENT_URI, "data"), ProfileQuery.PROJECTION, "mimetype = ?", new String[]{"vnd.android.cursor.item/email_v2"}, "is_primary DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(0));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        this.mEmailView.setAdapter(new ArrayAdapter(this, 17367050, emailAddressCollection));
    }
}
