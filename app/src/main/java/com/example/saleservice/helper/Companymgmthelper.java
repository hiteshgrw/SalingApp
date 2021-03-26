package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;


public class Companymgmthelper extends SQLiteOpenHelper {
//    public static final String  pass = "bookworldempire";
//    public static Companymgmthelper instance;
    public static String DATABASE_NAME = "directory.db";
    public static String TABLE_NAME = "Companynamedetail";
    public Companymgmthelper(@Nullable Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/" +DATABASE_NAME,null,1);
    }
//    public static synchronized Companymgmthelper getInstance(Context context)
//    {
//        if(instance==null)
//            instance = new Companymgmthelper(context);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
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
    public boolean update(String oldname,String newname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",newname);
        db.update(TABLE_NAME,contentValues,"NAME = ?",new String[]{ oldname });
        return true;
    }
    public  boolean delete(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer result = db.delete(TABLE_NAME,"NAME = ?",new String[]{ name });
        if(result>0)
        {
            return  true;
        }
        else
            return false;
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
        else
            return 0;
    }
}
