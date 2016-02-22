package com.ls.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ftunram.secsurf.toolkit.DatabaseHelper;
import com.ftunram.secsurf.toolkit.User;
import com.ls.directoryselectordemo.R;

public class PassActivity extends Activity {
    DatabaseHelper helper;
    EditText pass;
    EditText pass2;
    EditText user;

    public PassActivity() {
        this.helper = new DatabaseHelper(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        this.user = (EditText) findViewById(R.id.username);
        this.pass = (EditText) findViewById(R.id.password);
        this.pass2 = (EditText) findViewById(R.id.password2);
    }

    public void okClick(View v) {
        String username = this.user.getText().toString();
        String password = this.pass.getText().toString();
        if (password.equals(this.pass2.getText().toString())) {
            User ne = new User();
            ne.setPassword(password);
            ne.setUsername(username);
            this.helper.insertUser(ne);
            return;
        }
        Toast.makeText(this, "Password tidak cocok!", 0).show();
    }
}
