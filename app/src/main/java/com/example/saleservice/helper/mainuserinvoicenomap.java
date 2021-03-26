package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

public class mainuserinvoicenomap extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "invoicedet.db";
    public static String TABLE_NAME;
    public Context mcontext;
    public mainuserinvoicenomap(@Nullable Context context,String dname,Integer uname) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/User"+uname+"/Schooldata/" + DATABASE_NAME,null,1);
        TABLE_NAME = dname;
        mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (INVNO INTEGER PRIMARY KEY,SCHNAME TEXT,CLSNAME TEXT,BGTOTAL REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(Integer id,String sname,String cname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("INVNO",id);
        contentValues.put("SCHNAME",sname);
        contentValues.put("CLSNAME",cname);
        contentValues.put("BGTOTAL",0.0);
        Long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
        {
            return false;
        }
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
        Integer nod = db.delete(TABLE_NAME,"INVNO = ?",new String[]{String.valueOf(id)});
        if(nod>0)
            return true;
        else
            return false;
    }
    public void updatetotalamt(Integer id,Double total)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BGTOTAL",total);
        db.update(TABLE_NAME,contentValues,"INVNO = ?",new String[]{String.valueOf(id)});
    }
}
