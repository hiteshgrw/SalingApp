package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.example.saleservice.classes.Classmgmtclass;

public class Classmgmthelper extends SQLiteOpenHelper {
//    public static Classmgmthelper instance;
    public static String DATABASE_NAME;
    public static String TABLE_NAME;
//    public static final String pass = "bookworldempire";
    public Classmgmthelper(@Nullable Context context, String dname, String name) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/" + dname + ".db",null,1);
        DATABASE_NAME = dname + ".db";
        TABLE_NAME = name;
    }
//    public static synchronized Classmgmthelper getInstance(Context context,String dname, String name)
//    {
//        if(instance==null)
//            instance = new Classmgmthelper(context,dname,name);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+ TABLE_NAME + " (ID INTEGER,NAME TEXT PRIMARY KEY,DISCOUNT REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(Integer Id,String name, Double dis)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",Id);
        contentValues.put("NAME",name);
        contentValues.put("DISCOUNT",dis);
        Long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
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
    public boolean update(Integer id,String name,Double dis)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("DISCOUNT",dis);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{ String.valueOf(id) });
        return true;
    }
    public void droptable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_NAME);
    }
    public Integer getid(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where NAME = '"+name+"'",null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else {
            return 0;
        }
    }
    public String getname(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = "+id,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getString(1);
        }
        else {
            return null;
        }
    }
    public Double getdis(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = "+id,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getDouble(2);
        }
        else {
            return 0.0;
        }
    }
//    public Classmgmtclass getfdata(Integer id)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = "+id,null);
//        if(cursor.getCount()>0)
//        {
//            cursor.moveToFirst();
//            Classmgmtclass sample = new Classmgmtclass(cursor.getInt(0),cursor.getString(1),cursor.getDouble(2));
//            return sample;
//        }
//        else {
//            return null;
//        }
//    }
}
