package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class schoolcompanymap extends SQLiteOpenHelper {
    public static String DATABASE_NAME;
    public static String TABLE_NAME;
//    public static final String pass = "bookworldempire";
//    public static schoolcompanymap instance;
    public schoolcompanymap(@Nullable Context context, String name) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/" + name + ".db",null,1);
        TABLE_NAME = "schoolcmpmap";
    }
//    public static synchronized schoolcompanymap getInstance(Context context,String name)
//    {
//        if(instance == null)
//            instance = new schoolcompanymap(context, name);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (NAME TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public List<String> display()
    {
        List<String> namelist = new ArrayList<>();
        namelist.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                    namelist.add(cursor.getString(0));
                return namelist;
            } else
                return null;
        }catch (Exception e)
        {
            return null;
        }
    }
}
