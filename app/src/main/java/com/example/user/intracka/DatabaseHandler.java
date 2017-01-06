package com.example.user.intracka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/4/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DB_NAME = "intrack";
    private static final String TABLE_NAME = "user_mast";

    public DatabaseHandler (Context context)
    {
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +
                "(usr_id TEXT PRIMARY KEY, site_id TEXT, username TEXT, password text)";

        db.execSQL(CREATE_TABLE_SQL);

        String SQL2 = "CREATE TABLE user_access_hist (rec_id TEXT PRIMARY KEY AUTO_INCREMENT,usr_id TEXT, login_date TEXT," +
                "login_time TEXT,login_loc TEXT, logout_date TEXT,logout_time TEXT,logout_loc TEXT,device_imei_num TEXT" +
                "duration TEXT)";

        db.execSQL(SQL2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public void addRecord(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put("usr_id", user.getUsr_id());
        content.put("site_id",user.getSite_id());
        content.put("username",user.getUsername());
        content.put("password",user.getPassword());

        db.insert(TABLE_NAME, null,content);
        db.close();
    }

    public void addHist(AccessHistory ah){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put("usr_id",ah.getUsr_id());
        content.put("login_date",ah.getLogin_date());
        content.put("login_time",ah.getLogin_time());
        content.put("login_loc",ah.getLogin_loc());
        content.put("device_imei_num",ah.getDevice_imei_num1());

        db.insert("user_access_hist",null,content);

    }


    public void clearDb1()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE from " + TABLE_NAME,null).moveToFirst();
        db.close();
    }

    public void clearDb2()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE from user_access_hist" ,null).moveToFirst();
        db.close();
    }


    public User getUser (String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"usr_id","site_id","password"},"username=?",
                new String[]{username},
                null,null,null,null);

        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            User user = new User((cursor.getString(0)),cursor.getString(1),username,cursor.getString(2));
            return user;
        }
        return null;
    }

//    public List<User> getAllUser(){
//        List<User>userList = new ArrayList<User>();
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_NAME,null);
//
//        if (cursor.moveToFirst()){
//            do{
//                User user = new User();
//                user.setUsr_id(cursor.getString(0));
//                user.setSite_id(cursor.getString(1));
//                user.setUsername(cursor.getString(2));
//                user.setPassword(cursor.getString(3));
//
//                userList.add(user);
//            }while (cursor.moveToNext());
//
//            return userList;
//        }
//
//        return null;
//    }
}
