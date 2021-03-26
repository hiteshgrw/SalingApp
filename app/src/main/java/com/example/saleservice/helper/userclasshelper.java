package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.example.saleservice.classes.schoolmgmtclass;

public class userclasshelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "localschool.db";
    public static String TABLE_NAME = "schoolname";
    public userclasshelper(@Nullable Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/"+DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (ID INTEGER,NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(Integer id,String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("NAME",name);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public schoolmgmtclass getdata()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                schoolmgmtclass schclass = new schoolmgmtclass(cursor.getString(1),cursor.getInt(0));
                return schclass;
            }
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
