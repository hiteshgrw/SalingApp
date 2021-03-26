package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ordercreatorhelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "ordermanager.db";
    public static String TABLE_NAME = "ordercreatecheck";

    public ordercreatorhelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,SCHNAME TEXT,FLAG INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String name,Integer flag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("SCHNAME",name);
        contentValues.put("FLAG",flag);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public Integer getflag(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where SCHNAME = '" + name + "'", null);
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                return cursor.getInt(2);
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
