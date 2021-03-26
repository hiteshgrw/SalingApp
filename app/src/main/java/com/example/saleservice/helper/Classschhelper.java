package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.example.saleservice.classes.schoolmgmtclass;

import java.util.ArrayList;
import java.util.List;

public class Classschhelper extends SQLiteOpenHelper {
//    public static Classschhelper instance;
    public static String DATABASE_NAME;
    public static String TABLE_NAME;
//    public static final String pass = "bookworldempire";
    public Classschhelper(@Nullable Context context,String dname,String name) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/" + dname + ".db",null,1);
        TABLE_NAME = name;
        DATABASE_NAME = dname + ".db";
    }

//    public static synchronized Classschhelper getInstance(Context context,String dname,String name)
//    {
//        if(instance==null)
//            instance = new Classschhelper(context,dname,name);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,CLASSNAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("CLASSNAME",name);
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
    public boolean delete(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer reesult = db.delete(TABLE_NAME,"ID = ?",new String[]{ String.valueOf(id) });
        if(reesult > 0)
            return true;
        else
            return false;
    }
    public Integer getid(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where CLASSNAME = '"+name+"'",null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else
            return 0;
    }
    public String getname(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = "+id,null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getString(1);
        }
        else
            return null;
    }
    public boolean update(Integer id, String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CLASSNAME",name);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{ String.valueOf(id) });
        return true;
    }
    public Integer getsize()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
            return cursor.getCount();
        }
        catch (Exception e)
        {
            return  0;
        }
    }
    public List<String> getdata(Integer id)
    {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    if(cursor.getInt(0)!=id)
                        list.add(cursor.getString(1));
                }
                return list;
            }
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
