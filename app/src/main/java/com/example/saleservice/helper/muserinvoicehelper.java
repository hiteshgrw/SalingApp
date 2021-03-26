package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

public class muserinvoicehelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "invoicedet.db";
    public static String TABLE_NAME;
    public muserinvoicehelper(@Nullable Context context,String name,Integer uname) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+uname+"/Schooldata/" + DATABASE_NAME,null,1);
        TABLE_NAME = name;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (SNO INTEGER PRIMARY KEY AUTOINCREMENT,BNAME TEXT,CNAME TEXT,BQUAN INTEGER,BPRICE REAL,BDIS REAL,BTOTAL REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(String bname,String cname,Integer bquan,Double bprice,Double bdis,Double btotal)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("BNAME",bname);
        contentValues.put("CNAME",cname);
        contentValues.put("BQUAN",bquan);
        contentValues.put("BPRICE",bprice);
        contentValues.put("BDIS",bdis);
        contentValues.put("BTOTAL",btotal);
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
    public void droptable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_NAME);
    }
}
