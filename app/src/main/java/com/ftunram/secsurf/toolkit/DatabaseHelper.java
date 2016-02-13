package com.ftunram.secsurf.toolkit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ice on 2/13/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="users.db";
    public static final String TABLE_NAME="users";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_USERNAME="username";
    public static final String COLUMN_PASSWORD="password";

    public static final String TABLE_CREATE="create table users (id integer primary key not null ,"+
            "username text not null, password text not null);";

    SQLiteDatabase db;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
        db.execSQL(TABLE_CREATE);}
        catch (SQLiteException e){

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query="DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
    public void insertUser(User user){
        db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        String query = "select * from users";
        Cursor cursor=db.rawQuery(query,null);
        int count=cursor.getCount();
        values.put(COLUMN_ID,count);
        values.put(COLUMN_PASSWORD,user.getPassword());
        values.put(COLUMN_USERNAME,user.getUsername());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public String searchPass(String username){
        db=this.getReadableDatabase();
        String query="Select username,password from "+TABLE_NAME;
        Cursor cursor=db.rawQuery(query,null);
        String a,b;
        b="not found";
        if(cursor.moveToFirst()){
            do{
                a=cursor.getString(0);
                if(a.equals(username)){
                    b=cursor.getString(1);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        return b;
    }
}
