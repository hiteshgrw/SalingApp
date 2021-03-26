package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import androidx.annotation.Nullable;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class booklisthelper extends SQLiteOpenHelper {
//    public static booklisthelper instance;
    public static String DATABASE_NAME;
    public String TABLE_NAME;
//    public static final String pass = "bookworldempire";
//    public static synchronized booklisthelper getInstance(Context context,String dname,String tname)
//    {
//        if(instance == null)
//            instance = new booklisthelper(context,dname,tname);
//        return instance;
//    }
    public booklisthelper(@Nullable Context context,String dname,String tname) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/" + dname + ".db" ,null,1);
        DATABASE_NAME = dname + ".db";
        TABLE_NAME = tname;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (BNAME TEXT PRIMARY KEY,CNAME TEXT,BPRICE REAL,BQUAN INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(String bname,String cname,Double bprice,Integer quan)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("BNAME",bname);
        contentValues.put("CNAME",cname);
        contentValues.put("BPRICE",bprice);
        contentValues.put("BQUAN",quan);
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
    public boolean update(String name,Double price,Integer quan)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BPRICE",price);
        contentValues.put("BQUAN",quan);
        db.update(TABLE_NAME,contentValues,"BNAME = ?",new String[]{ name });
        return true;
    }
    public boolean delete(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer res = db.delete(TABLE_NAME,"BNAME = ?",new String[]{ name });
        if(res>0)
            return true;
        else
            return false;
    }
}
