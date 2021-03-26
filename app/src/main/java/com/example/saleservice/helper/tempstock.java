package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class tempstock extends SQLiteOpenHelper {
    public static String DATABASE_NAME;
    public static String TABLE_NAME;
    public tempstock(@Nullable Context context, String name) {
        super(context, "booktempdata.db",null,1);
        TABLE_NAME = name;
        DATABASE_NAME = "booktempdata.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME + " (BNAME TEXT PRIMARY KEY,BPRICE REAL,PURQUAN INTEGER,SALEQUAN INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void setquan0()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = display();
        if(cursor.getCount()>0)
        {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                updatesale(name,0);
            }
        }
    }
    public boolean insert(String name,Double price,Integer quant,Integer sqaunt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("BNAME",name);
        contentValues.put("BPRICE",price);
        contentValues.put("PURQUAN",quant);
        contentValues.put("SALEQUAN",sqaunt);
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
    public double getprice(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where BNAME = '"+name+"'",null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            Double price = cursor.getDouble(1);
            return price;
        }
        else
            return 0.0;
    }
    public Integer getpurstock(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where BNAME = '"+name+"'",null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(2);
        }
        else
            return 0;
    }
    public Integer getsalestock(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where BNAME = '"+name+"'",null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(3);
        }
        else
            return 0;
    }
    public void update(String name,Integer prstck,Integer slstck,Double price)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PURQUAN",prstck);
        contentValues.put("SALEQUAN",slstck);
        contentValues.put("BPRICE",price);
        db.update(TABLE_NAME,contentValues,"BNAME = ?",new String[]{name});
    }
    public void updateprice(String name,Double prc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BPRICE",prc);
        db.update(TABLE_NAME,contentValues,"BNAME = ?",new String[]{name});
    }
    public void updatesale(String name,Integer stck)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SALEQUAN",stck);
        db.update(TABLE_NAME,contentValues,"BNAME = ?",new String[]{name});
    }
    public void updatepurstock(String name,Integer stck)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PURQUAN",stck);
        db.update(TABLE_NAME,contentValues,"BNAME = ?",new String[]{name});
    }
}
