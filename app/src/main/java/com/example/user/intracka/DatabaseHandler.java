package com.example.user.intracka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

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

        String SQL2 = "CREATE TABLE user_access_hist (usr_id TEXT, login_date TEXT," +
                "login_time TEXT,login_loc TEXT, logout_date TEXT,logout_time TEXT,logout_loc TEXT,device_imei_num TEXT" +
                ",duration TEXT)";

        db.execSQL(SQL2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS user_access_hist");

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

        content.put("logout_date","");
        content.put("logout_time","");
        content.put("logout_loc","");

        content.put("device_imei_num",ah.getDevice_imei_num1());

        content.put("duration","");

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

            //Log.d("deded",cursor.getString(0));
            return user;
        }
        return null;
    }

    public int getHistoryCount(){
        String countQuery = "SELECT  * FROM user_access_hist" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getUserCount(){
        String countQuery = "SELECT  * FROM user_mast" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public List<User> getAllUser(){
        List<User>userList = new ArrayList<User>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_NAME,null);

        if (cursor.moveToFirst()){
            do{
                User user = new User();
                user.setUsr_id(cursor.getString(0));
                user.setSite_id(cursor.getString(1));
                user.setUsername(cursor.getString(2));
                user.setPassword(cursor.getString(3));

                userList.add(user);
            }while (cursor.moveToNext());

            return userList;
        }

        return null;
    }

    public List<AccessHistory> getAllHist(){
        List<AccessHistory>histList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from user_access_hist",null);

        if (cursor.moveToFirst()){
            do{
                AccessHistory ah = new AccessHistory();
                ah.setUsr_id(cursor.getString(0));
                ah.setLogin_date(cursor.getString(1));
                ah.setLogin_time(cursor.getString(2));
                ah.setLogin_loc(cursor.getString(3));

                ah.setLogout_date(cursor.getString(cursor.getColumnIndex("logout_date")));
                ah.setLogout_time(cursor.getString(cursor.getColumnIndex("logout_time")));
                ah.setLogout_loc(cursor.getString(cursor.getColumnIndex("logout_loc")));

                ah.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
                ah.setDevice_imei_num1(cursor.getString(cursor.getColumnIndex("device_imei_num")));


                histList.add(ah);
            }while (cursor.moveToNext());

            return histList;
        }

        return null;
    }


    public void updateHist(String loc,String duration){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();
        DateFormat t = new SimpleDateFormat("HH:mm:ss");

        ContentValues cv = new ContentValues();

        cv.put("logout_date",df.format(dateobj));
        cv.put("logout_time",t.format(dateobj));
        cv.put("logout_loc",loc);
        cv.put("duration",duration);


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT rowid FROM user_access_hist",null);
        if(cursor.moveToNext())
            cursor.moveToLast();
        Log.d("cursor",cursor.getString(0));
        db.update("user_access_hist",cv,"rowid="+cursor.getString(0),null);

//        String query  = String.format("UPDATE user_access_hist SET logout_date=" + df.format(dateobj) + "logout_time=" + t, (dateobj) + "logout_loc=" + loc + "duration =" + duration + "WHERE rec_id = (SELECT MAX(rec_id) FROM user_access_hist)" );

    }

    public String getDuration(){
        String time[] = new String[2];
        Date dateobj = new Date();
        DateFormat t = new SimpleDateFormat("HH:mm:ss");

        time[0] = t.format(dateobj);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT rowid FROM user_access_hist",null);
        if(cursor.moveToNext())
            cursor.moveToLast();

        Cursor cursor1 = db.rawQuery("SELECT login_time from user_access_hist where rowid=" +  cursor.getString(0),null);
        if(cursor1.moveToNext())
            cursor1.moveToLast();

        Log.d("c",time[0]);

        time[1] = cursor1.getString(0);

        Log.d("c1",time[1]);


        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        try{
            Date d1 = format.parse(time[0]);
            Date d2 = format.parse(time[1]);
            long seconds = (d1.getTime() - d2.getTime())/1000;

            Log.d("diff",String.valueOf(seconds));

            long s = seconds % 60;
            long m = (seconds / 60) % 60;
            long h = (seconds / (60 * 60)) % 24;

//            Log.d("asdf",String.format("%d:%02d:%02d", h,m,s));
            return String.format("%d:%02d:%02d", h,m,s);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public void deleteFirstRow(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT rowid FROM user_access_hist",null);
        if(cursor.moveToNext())
            cursor.moveToFirst();

//        Cursor c = db.rawQuery("DELETE FROM user_access_hist where rowid="+cursor.getString(0),null);
        db.execSQL("DELETE FROM user_access_hist where rowid="+cursor.getString(0));
        db.close();
    }
}
