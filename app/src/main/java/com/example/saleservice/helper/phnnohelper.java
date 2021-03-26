package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

public class phnnohelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "directory.db";
    public static String TABLE_NAME;
//    public static final String pass = "bookworldempire";
//    public static phnnohelper instance;
    public phnnohelper(@Nullable Context context,String name) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/" + DATABASE_NAME,null,1);
        TABLE_NAME = name;
    }
//    public static synchronized phnnohelper getInstance(Context context,String name)
//    {
//        if(instance == null)
//            instance = new phnnohelper(context, name);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (CONTACT INTEGER(10) PRIMARY KEY,CLASS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(Long num,String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("CONTACT",num);
        contentValues.put("CLASS",name);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public Cursor display()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
}
