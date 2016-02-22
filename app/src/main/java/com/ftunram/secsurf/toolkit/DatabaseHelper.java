package com.ftunram.secsurf.toolkit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USERNAME = "username";
    public static final String DATABASE_NAME = "users.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CREATE = "create table users (id integer primary key not null ,username text not null, password text not null);";
    public static final String TABLE_NAME = "users";
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE);
        } catch (SQLiteException e) {
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void insertUser(User user) {
        this.db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, Integer.valueOf(this.db.rawQuery("select * from users", null).getCount()));
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_USERNAME, user.getUsername());
        this.db.insert(TABLE_NAME, null, values);
        this.db.close();
    }

    public String searchPass(String username) {
        this.db = getReadableDatabase();
        Cursor cursor = this.db.rawQuery("Select username,password from users", null);
        String b = "not found";
        if (!cursor.moveToFirst()) {
            return b;
        }
        while (!cursor.getString(0).equals(username)) {
            if (!cursor.moveToNext()) {
                return b;
            }
        }
        return cursor.getString(DATABASE_VERSION);
    }
}
