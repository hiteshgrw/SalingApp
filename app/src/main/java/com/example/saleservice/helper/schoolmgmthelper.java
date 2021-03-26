package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

public class schoolmgmthelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "directory.db";
    private String TABLE_NAME = "Schoolnamedetail";
//    public static final String pass = "bookworldempire";
//    public static schoolmgmthelper instance;
    public schoolmgmthelper(@Nullable Context context) {
        super(context,Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Bookdata/" + DATABASE_NAME,null,1);
    }
//    public static synchronized schoolmgmthelper getInstance(Context context)
//    {
//        if(instance == null)
//            instance = new schoolmgmthelper(context);
//        return instance;
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table if not exists "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(String sname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",sname);
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
    public boolean update(Integer id,String nname){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",nname);
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{ String.valueOf(id) });
        return true;
    }
    public Integer delete(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"ID = ?",new String[]{ String.valueOf(id) });
    }
    public Integer getid(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where NAME = '" + name + "'",null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        else
            return 0;
    }
    public String getname(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where ID = " + id,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getString(1);
        }
        else
            return null;
    }
}
