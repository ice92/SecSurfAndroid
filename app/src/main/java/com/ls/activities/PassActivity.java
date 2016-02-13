package com.ls.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ftunram.secsurf.toolkit.DatabaseHelper;
import com.ftunram.secsurf.toolkit.User;

public class PassActivity extends Activity {
    DatabaseHelper helper=new DatabaseHelper(this);
    EditText user,pass,pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        user=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.password);
        pass2=(EditText)findViewById(R.id.password2);
    }
    public void okClick(View v){


        String username=user.getText().toString();
        String password=pass.getText().toString();
        String password2=pass2.getText().toString();

        if(!password.equals(password2)){
            Toast p=Toast.makeText(this,"Password tidak cocok!",Toast.LENGTH_SHORT);
            p.show();
        }
        else{
            User ne=new User();
            ne.setPassword(password);
            ne.setUsername(username);
            helper.insertUser(ne);
        }

    }
}
