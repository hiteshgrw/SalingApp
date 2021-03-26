package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

public class userhelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "directory.db";
    public static String TABLE_NAME = "userdet";
    public userhelper(@Nullable Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/" + DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UEMAIL TEXT,UUID TEXT,PASS TEXT,SCHID INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String email,String uid,String pass,Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("UEMAIL",email);
        contentValues.put("UUID",uid);
        contentValues.put("PASS",pass);
        contentValues.put("SCHID",id);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public boolean existscheck(String email)
    {
        int flag = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    if(email.equals(cursor.getString(1)))
                        flag = 1;
                }
            }
            else
                flag = 0;
        }catch (Exception e)
        {
            return false;
        }
        if(flag == 0)
            return false;
        else
            return true;
    }
    public Cursor display()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
    public void delete(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"ID = ?",new String[]{String.valueOf(id)});
    }
    public void updatepass(Integer id,String pass)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PASS",pass);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{String.valueOf(id)});
    }
    public Integer getid(String email,Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where UEMAIL = '"+email+"' AND SCHID = "+id,null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
            else
                return 0;
        }
        catch (Exception e)
        {
            return 0;
        }
    }
    public Integer getschid(String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where UEMAIL = '"+email+"'",null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                return cursor.getInt(4);
            }
            else
                return 0;
        }
        catch (Exception e)
        {
            return 0;
        }
    }
    public Integer getschiduid(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = "+id,null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                return cursor.getInt(4);
            }
            else
                return 0;
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
